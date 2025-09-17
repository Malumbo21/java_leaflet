package io.github.makbn.jlmap.vaadin.layer;

import com.vaadin.flow.component.page.PendingJavaScriptResult;
import io.github.makbn.jlmap.JLMapEventHandler;
import io.github.makbn.jlmap.engine.JLClientToServerTransporter;
import io.github.makbn.jlmap.engine.JLWebEngine;
import io.github.makbn.jlmap.exception.JLException;
import io.github.makbn.jlmap.geojson.JLGeoJsonContent;
import io.github.makbn.jlmap.geojson.JLGeoJsonFile;
import io.github.makbn.jlmap.geojson.JLGeoJsonURL;
import io.github.makbn.jlmap.layer.leaflet.LeafletGeoJsonLayerInt;
import io.github.makbn.jlmap.listener.JLAction;
import io.github.makbn.jlmap.model.JLGeoJson;
import io.github.makbn.jlmap.model.JLGeoJsonOptions;
import io.github.makbn.jlmap.model.builder.JLGeoJsonObjectBuilder;
import io.github.makbn.jlmap.vaadin.engine.JLVaadinClientToServerTransporter;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Vaadin implementation of the GeoJSON layer for managing geographic data overlays.
 * <p>
 * This implementation provides GeoJSON support for Vaadin-based applications, handling
 * the loading, styling, and interaction with geographic data from various sources.
 * It leverages Vaadin's Element.executeJs() for asynchronous JavaScript execution
 * and PendingJavaScriptResult for handling callbacks.
 * </p>
 * <h3>Implementation Details:</h3>
 * <ul>
 *   <li><strong>Data Loading</strong>: Supports files, URLs, and direct content strings</li>
 *   <li><strong>JavaScript Bridge</strong>: Uses JLVaadinClientToServerTransporter for callbacks</li>
 *   <li><strong>Event Handling</strong>: Supports click, double-click, add, and remove events</li>
 *   <li><strong>Error Handling</strong>: Enhanced error handling with logging for Vaadin context</li>
 *   <li><strong>Thread Model</strong>: Operates on Vaadin UI thread with async execution</li>
 * </ul>
 * <p>
 * <strong>Thread Safety:</strong> All operations are automatically marshaled to the Vaadin UI thread.
 * </p>
 *
 * @author Matt Akbarian  (@makbn)
 */
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class JLVaadinGeoJsonLayer extends JLVaadinLayer implements LeafletGeoJsonLayerInt {

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

    /**
     * JavaScript-to-Java communication bridge for event callbacks.
     * <p>
     * This transporter handles the conversion of JavaScript events to Java method calls,
     * enabling interactive functionality for GeoJSON features.
     * </p>
     */
    @Getter
    JLClientToServerTransporter clientToServer;

    /**
     * Constructs a new Vaadin GeoJSON layer with the specified engine and callback handler.
     * <p>
     * Initializes all data loaders, ID generator, and sets up the JavaScript-to-Java
     * communication bridge for handling user interactions and events. The bridge is
     * configured to work with Vaadin's asynchronous JavaScript execution model.
     * </p>
     *
     * @param engine the Vaadin web engine for JavaScript execution
     * @param callbackHandler the event handler for managing object callbacks
     */
    public JLVaadinGeoJsonLayer(JLWebEngine<PendingJavaScriptResult> engine,
                                JLMapEventHandler callbackHandler) {
        super(engine, callbackHandler);
        this.fromUrl = new JLGeoJsonURL();
        this.fromFile = new JLGeoJsonFile();
        this.fromContent = new JLGeoJsonContent();
        this.idGenerator = new AtomicInteger();
        this.clientToServer = new JLVaadinClientToServerTransporter(engine::executeScript);
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

    /**
     * Removes a GeoJSON object from the map by its unique identifier.
     * <p>
     * <strong>Vaadin-specific behavior:</strong> This implementation includes
     * enhanced error handling with logging, returning false if the removal fails
     * rather than propagating exceptions.
     * </p>
     *
     * @inheritDoc
     */
    @Override
    public boolean removeGeoJson(@NonNull String id) {
        try {
            engine.executeScript(removeLayerWithUUID(id));
            callbackHandler.remove(JLGeoJson.class, id);
            return true;
        } catch (RuntimeException e) {
            log.error("Failed to remove GeoJSON object with id: {}", id, e);
            return false;
        }
    }

    /**
     * Adds a GeoJSON object to the map from a JSON string.
     *
     * @param geoJson the GeoJSON string
     * @param options custom styling and configuration options
     * @return the added JLGeoJson object
     */
    @NonNull
    private JLGeoJson addGeoJson(@NonNull String geoJson, JLGeoJsonOptions options) {
        String elementUniqueName = getElementUniqueName(JLGeoJson.class, idGenerator.incrementAndGet());
        JLGeoJsonObjectBuilder builder = new JLGeoJsonObjectBuilder()
                .setTransporter(getTransporter())
                .setUuid(elementUniqueName)
                .setGeoJson(geoJson)
                .withGeoJsonOptions(options)
                .withBridge(clientToServer)
                .withCallbacks(jlCallbackBuilder -> {
                    jlCallbackBuilder.on(JLAction.CLICK);
                    jlCallbackBuilder.on(JLAction.DOUBLE_CLICK);
                    jlCallbackBuilder.on(JLAction.ADD);
                    jlCallbackBuilder.on(JLAction.REMOVE);
                });
        var obj = builder.buildJLObject();
        engine.executeScript(builder.buildJsElement());
        callbackHandler.addJLObject(elementUniqueName, obj);
        return obj;
    }

}
