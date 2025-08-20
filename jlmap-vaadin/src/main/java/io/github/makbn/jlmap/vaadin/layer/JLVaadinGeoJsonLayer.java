package io.github.makbn.jlmap.vaadin.layer;

import com.vaadin.flow.component.page.PendingJavaScriptResult;
import io.github.makbn.jlmap.JLMapCallbackHandler;
import io.github.makbn.jlmap.engine.JLWebEngine;
import io.github.makbn.jlmap.exception.JLException;
import io.github.makbn.jlmap.geojson.JLGeoJsonContent;
import io.github.makbn.jlmap.geojson.JLGeoJsonFile;
import io.github.makbn.jlmap.geojson.JLGeoJsonObject;
import io.github.makbn.jlmap.geojson.JLGeoJsonURL;
import io.github.makbn.jlmap.layer.leaflet.LeafletGeoJsonLayerInt;
import lombok.AccessLevel;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;

import java.io.File;

/**
 * Represents the GeoJson (other) layer on Leaflet map.
 *
 * @author Matt Akbarian  (@makbn)
 */
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class JLVaadinGeoJsonLayer extends JLVaadinLayer implements LeafletGeoJsonLayerInt {
    JLGeoJsonURL fromUrl;
    JLGeoJsonFile fromFile;
    JLGeoJsonContent fromContent;

    public JLVaadinGeoJsonLayer(JLWebEngine<PendingJavaScriptResult> engine,
                                JLMapCallbackHandler callbackHandler) {
        super(engine, callbackHandler);
        this.fromUrl = new JLGeoJsonURL();
        this.fromFile = new JLGeoJsonFile();
        this.fromContent = new JLGeoJsonContent();
    }

    @Override
    public JLGeoJsonObject addFromFile(@NonNull File file) throws JLException {
        String json = fromFile.load(file);
        return addGeoJson(json);
    }

    @Override
    public JLGeoJsonObject addFromUrl(@NonNull String url) throws JLException {
        String json = fromUrl.load(url);
        return addGeoJson(json);
    }

    @Override
    public JLGeoJsonObject addFromContent(@NonNull String content)
            throws JLException {
        String json = fromContent.load(content);
        return addGeoJson(json);
    }

    @Override
    public boolean removeGeoJson(@NonNull JLGeoJsonObject object) {
        // TODO impement
        return false;
    }

    private JLGeoJsonObject addGeoJson(String jlGeoJsonObject) {
        // TODO implement
        return null;
    }
}
