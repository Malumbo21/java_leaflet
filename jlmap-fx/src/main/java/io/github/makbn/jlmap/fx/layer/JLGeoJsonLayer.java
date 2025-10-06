package io.github.makbn.jlmap.fx.layer;

import io.github.makbn.jlmap.JLMapEventHandler;
import io.github.makbn.jlmap.engine.JLClientToServerTransporter;
import io.github.makbn.jlmap.engine.JLWebEngine;
import io.github.makbn.jlmap.exception.JLException;
import io.github.makbn.jlmap.fx.engine.JLJavaFXClientToServerTransporter;
import io.github.makbn.jlmap.geojson.JLGeoJsonContent;
import io.github.makbn.jlmap.geojson.JLGeoJsonFile;
import io.github.makbn.jlmap.geojson.JLGeoJsonURL;
import io.github.makbn.jlmap.layer.leaflet.LeafletGeoJsonLayerInt;
import io.github.makbn.jlmap.listener.JLAction;
import io.github.makbn.jlmap.model.JLGeoJson;
import io.github.makbn.jlmap.model.JLGeoJsonOptions;
import io.github.makbn.jlmap.model.builder.JLGeoJsonObjectBuilder;
import lombok.NonNull;

import java.io.File;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * JavaFX implementation of the GeoJSON layer for managing geographic data overlays.
 * <p>
 * This implementation provides GeoJSON support for JavaFX-based maps, handling
 * the loading, styling, and interaction with geographic data from various sources.
 * It leverages JavaFX's WebEngine for JavaScript execution and synchronous communication.
 * </p>
 * <h3>Implementation Details:</h3>
 * <ul>
 *   <li><strong>Data Loading</strong>: Supports files, URLs, and direct content strings</li>
 *   <li><strong>JavaScript Bridge</strong>: Uses JLJavaFXClientToServerTransporter for callbacks</li>
 *   <li><strong>Event Handling</strong>: Supports click, double-click, add, and remove events</li>
 *   <li><strong>Thread Model</strong>: Executes on JavaFX Application Thread</li>
 * </ul>
 * <p>
 * <strong>Thread Safety:</strong> All operations must be performed on the JavaFX Application Thread.
 * </p>
 *
 * @author Matt Akbarian  (@makbn)
 * @since 2.0.0
 */
public class JLGeoJsonLayer extends JLLayer implements LeafletGeoJsonLayerInt {

    /**
     * URL-based GeoJSON loader for remote data sources
     */
    JLGeoJsonURL fromUrl;

    /** File-based GeoJSON loader for local data sources */
    JLGeoJsonFile fromFile;

    /** Content-based GeoJSON loader for direct string input */
    JLGeoJsonContent fromContent;

    /** Atomic counter for generating unique element IDs */
    AtomicInteger idGenerator;

    /** JavaScript-to-Java communication bridge for event callbacks */
    JLClientToServerTransporter clientToServer;

    /**
     * Constructs a new JavaFX GeoJSON layer with the specified engine and callback handler.
     * <p>
     * Initializes all data loaders, ID generator, and sets up the JavaScript-to-Java
     * communication bridge for handling user interactions and events.
     * </p>
     *
     * @param engine the JavaFX web engine for JavaScript execution
     * @param callbackHandler the event handler for managing object callbacks
     */
    public JLGeoJsonLayer(JLWebEngine<Object> engine, JLMapEventHandler callbackHandler) {
        super(engine, callbackHandler);
        this.fromUrl = new JLGeoJsonURL();
        this.fromFile = new JLGeoJsonFile();
        this.idGenerator = new AtomicInteger();
        this.fromContent = new JLGeoJsonContent();
        // Initialize the JavaScript-to-Java bridge
        this.clientToServer = new JLJavaFXClientToServerTransporter(engine::executeScript);
    }

    /** @inheritDoc */
    @Override
    public JLGeoJson addFromFile(@NonNull File file) throws JLException {
        String json = fromFile.load(file);
        return addGeoJson(json, null);
    }

    /** @inheritDoc */
    @Override
    public JLGeoJson addFromFile(@NonNull File file, @NonNull JLGeoJsonOptions options) throws JLException {
        String json = fromFile.load(file);
        return addGeoJson(json, options);
    }

    /** @inheritDoc */
    @Override
    public JLGeoJson addFromUrl(@NonNull String url) throws JLException {
        String json = fromUrl.load(url);
        return addGeoJson(json, null);
    }

    /** @inheritDoc */
    @Override
    public JLGeoJson addFromUrl(@NonNull String url, @NonNull JLGeoJsonOptions options) throws JLException {
        String json = fromUrl.load(url);
        return addGeoJson(json, options);
    }

    /** @inheritDoc */
    @Override
    public JLGeoJson addFromContent(@NonNull String content) throws JLException {
        String json = fromContent.load(content);
        return addGeoJson(json, null);
    }

    /** @inheritDoc */
    @Override
    public JLGeoJson addFromContent(@NonNull String content, @NonNull JLGeoJsonOptions options) throws JLException {
        String json = fromContent.load(content);
        return addGeoJson(json, options);
    }

    /** @inheritDoc */
    @Override
    public boolean removeGeoJson(@NonNull String id) {
        engine.executeScript(removeLayerWithUUID(id));
        callbackHandler.remove(JLGeoJson.class, id);
        return true;
    }

    /**
     * Internal method to add GeoJSON content to the map with optional styling.
     * <p>
     * Creates a unique element name, builds the JavaScript representation, and
     * registers the object for event callbacks. Sets up standard interaction
     * events (click, double-click, add, remove).
     * </p>
     *
     * @param geoJsonContent the GeoJSON string content to add
     * @param options optional styling and configuration options (may be null)
     * @return the created JLGeoJson object
     */
    private JLGeoJson addGeoJson(String geoJsonContent, JLGeoJsonOptions options) {
        String elementUniqueName = getElementUniqueName(JLGeoJson.class, idGenerator.incrementAndGet());
        JLGeoJsonObjectBuilder builder = new JLGeoJsonObjectBuilder()
                .setUuid(elementUniqueName)
                .setGeoJson(geoJsonContent)
                .withGeoJsonOptions(options)
                .withBridge(clientToServer)
                .setTransporter(getTransporter())
                .withCallbacks(jlCallbackBuilder -> {
                    jlCallbackBuilder.on(JLAction.CLICK);
                    jlCallbackBuilder.on(JLAction.DOUBLE_CLICK);
                    jlCallbackBuilder.on(JLAction.ADD);
                    jlCallbackBuilder.on(JLAction.REMOVE);
                });
        JLGeoJson geoJson = builder.buildJLObject();
        engine.executeScript(builder.buildJsElement());
        callbackHandler.addJLObject(elementUniqueName, geoJson);
        return geoJson;
    }
}
