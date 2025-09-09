package io.github.makbn.jlmap.fx.layer;

import io.github.makbn.jlmap.JLMapCallbackHandler;
import io.github.makbn.jlmap.engine.JLTransporter;
import io.github.makbn.jlmap.engine.JLWebEngine;
import io.github.makbn.jlmap.exception.JLException;
import io.github.makbn.jlmap.geojson.JLGeoJsonContent;
import io.github.makbn.jlmap.geojson.JLGeoJsonFile;
import io.github.makbn.jlmap.geojson.JLGeoJsonURL;
import io.github.makbn.jlmap.layer.leaflet.LeafletGeoJsonLayerInt;
import io.github.makbn.jlmap.listener.JLAction;
import io.github.makbn.jlmap.model.JLGeoJson;
import io.github.makbn.jlmap.model.builder.JLGeoJsonObjectBuilder;
import lombok.NonNull;

import java.io.File;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Represents the GeoJson (other) layer on Leaflet map.
 *
 * @author Matt Akbarian  (@makbn)
 */
public class JLGeoJsonLayer extends JLLayer implements LeafletGeoJsonLayerInt {
    JLGeoJsonURL fromUrl;
    JLGeoJsonFile fromFile;
    JLGeoJsonContent fromContent;
    JLTransporter<Object> transporter;
    AtomicInteger idGenerator;

    public JLGeoJsonLayer(JLWebEngine<Object> engine,
                          JLMapCallbackHandler callbackHandler) {
        super(engine, callbackHandler);
        this.fromUrl = new JLGeoJsonURL();
        this.fromFile = new JLGeoJsonFile();
        this.idGenerator = new AtomicInteger();
        this.fromContent = new JLGeoJsonContent();
        this.transporter = () -> transport -> null;
    }

    @Override
    public JLGeoJson addFromFile(@NonNull File file) throws JLException {
        String json = fromFile.load(file);
        return addGeoJson(json);
    }

    @Override
    public JLGeoJson addFromUrl(@NonNull String url) throws JLException {
        String json = fromUrl.load(url);
        return addGeoJson(json);
    }

    @Override
    public JLGeoJson addFromContent(@NonNull String content)
            throws JLException {
        String json = fromContent.load(content);
        return addGeoJson(json);
    }

    @Override
    public boolean removeGeoJson(@NonNull String id) {
        engine.executeScript(removeLayerWithUUID(id));
        callbackHandler.remove(JLGeoJson.class, id);
        return true;
    }

    private JLGeoJson addGeoJson(String geoJsonContent) {
        String elementUniqueName = getElementUniqueName(JLGeoJson.class, idGenerator.incrementAndGet());
        JLGeoJsonObjectBuilder builder = new JLGeoJsonObjectBuilder()
                .setUuid(elementUniqueName)
                .setGeoJson(geoJsonContent)
                .setTransporter(transporter)
                .withCallbacks(jlCallbackBuilder -> {
                    jlCallbackBuilder.on(JLAction.CLICK);
                    jlCallbackBuilder.on(JLAction.DOUBLE_CLICK);
                    jlCallbackBuilder.on(JLAction.ADD);
                    jlCallbackBuilder.on(JLAction.REMOVE);
                });
        engine.executeScript(builder.buildJsElement());
        JLGeoJson geoJson = builder.buildJLObject();
        callbackHandler.addJLObject(elementUniqueName, geoJson);
        return geoJson;
    }
}
