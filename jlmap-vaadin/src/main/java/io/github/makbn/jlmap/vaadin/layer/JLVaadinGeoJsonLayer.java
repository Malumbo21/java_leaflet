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
 * Represents the GeoJson (other) layer on Leaflet map.
 *
 * @author Matt Akbarian  (@makbn)
 */
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class JLVaadinGeoJsonLayer extends JLVaadinLayer implements LeafletGeoJsonLayerInt {
    JLGeoJsonURL fromUrl;
    JLGeoJsonFile fromFile;
    JLGeoJsonContent fromContent;
    AtomicInteger idGenerator;
    /**
     * the transporter instance for JavaScript-to-Java communication.
     */
    @Getter
    JLClientToServerTransporter clientToServer;

    public JLVaadinGeoJsonLayer(JLWebEngine<PendingJavaScriptResult> engine,
                                JLMapEventHandler callbackHandler) {
        super(engine, callbackHandler);
        this.fromUrl = new JLGeoJsonURL();
        this.fromFile = new JLGeoJsonFile();
        this.fromContent = new JLGeoJsonContent();
        this.idGenerator = new AtomicInteger();
        this.clientToServer = new JLVaadinClientToServerTransporter(engine::executeScript);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public JLGeoJson addFromFile(@NonNull File file) throws JLException {
        String json = fromFile.load(file);
        return addGeoJson(json, null);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public JLGeoJson addFromFile(@NonNull File file, @NonNull JLGeoJsonOptions options) throws JLException {
        String json = fromFile.load(file);
        return addGeoJson(json, options);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public JLGeoJson addFromUrl(@NonNull String url) throws JLException {
        String json = fromUrl.load(url);
        return addGeoJson(json, null);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public JLGeoJson addFromUrl(@NonNull String url, @NonNull JLGeoJsonOptions options) throws JLException {
        String json = fromUrl.load(url);
        return addGeoJson(json, options);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public JLGeoJson addFromContent(@NonNull String content) throws JLException {
        String json = fromContent.load(content);
        return addGeoJson(json, null);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public JLGeoJson addFromContent(@NonNull String content, @NonNull JLGeoJsonOptions options) throws JLException {
        String json = fromContent.load(content);
        return addGeoJson(json, options);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean removeGeoJson(@NonNull String id) {
        try {
            engine.executeScript(removeLayerWithUUID(id));
            callbackHandler.remove(JLGeoJson.class, id);
            return true;
        } catch (RuntimeException e) {
            log.error(e.getMessage(), e);
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
    private JLGeoJson addGeoJson(String geoJson, JLGeoJsonOptions options) {
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
