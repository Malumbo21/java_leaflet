package io.github.makbn.jlmap.vaadin.layer;

import com.vaadin.flow.component.page.PendingJavaScriptResult;
import io.github.makbn.jlmap.JLMapCallbackHandler;
import io.github.makbn.jlmap.engine.JLWebEngine;
import io.github.makbn.jlmap.exception.JLException;
import io.github.makbn.jlmap.geojson.JLGeoJsonContent;
import io.github.makbn.jlmap.geojson.JLGeoJsonFile;
import io.github.makbn.jlmap.geojson.JLGeoJsonURL;
import io.github.makbn.jlmap.layer.leaflet.LeafletGeoJsonLayerInt;
import io.github.makbn.jlmap.model.JLGeoJson;
import io.github.makbn.jlmap.model.JLPolygon;
import io.github.makbn.jlmap.model.builder.JLGeoJsonObjectBuilder;
import lombok.AccessLevel;
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

    public JLVaadinGeoJsonLayer(JLWebEngine<PendingJavaScriptResult> engine,
                                JLMapCallbackHandler callbackHandler) {
        super(engine, callbackHandler);
        this.fromUrl = new JLGeoJsonURL();
        this.fromFile = new JLGeoJsonFile();
        this.fromContent = new JLGeoJsonContent();
        this.idGenerator = new AtomicInteger();
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
        try {
            engine.executeScript(removeLayerWithUUID(id));
            callbackHandler.remove(JLPolygon.class, id);
            return true;
        } catch (RuntimeException e) {
            log.error(e.getMessage(), e);
            return false;
        }
    }

    private JLGeoJson addGeoJson(String geoJson) {
        String elementUniqueName = getElementUniqueName(JLGeoJson.class, idGenerator.incrementAndGet());
        JLGeoJsonObjectBuilder builder = new JLGeoJsonObjectBuilder()
                .setTransporter(getTransporter())
                .setUuid(elementUniqueName)
                .setGeoJson(geoJson);
        engine.executeScript(builder.buildJsElement());
        var obj = builder.buildJLObject();
        callbackHandler.addJLObject(elementUniqueName, obj);
        return obj;
    }
}
