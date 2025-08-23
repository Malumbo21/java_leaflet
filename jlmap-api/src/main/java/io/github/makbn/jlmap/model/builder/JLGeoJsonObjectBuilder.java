package io.github.makbn.jlmap.model.builder;

import io.github.makbn.jlmap.model.JLGeoJson;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE)
public class JLGeoJsonObjectBuilder extends JLObjectBuilder<JLGeoJson, JLGeoJsonObjectBuilder> {
    String geoJson;

    @Override
    protected String getElementType() {
        return JLGeoJson.class.getSimpleName().toLowerCase();
    }

    @Override
    protected String getElementVarName() {
        return uuid;
    }

    public JLGeoJsonObjectBuilder setGeoJson(String geoJson) {
        this.geoJson = geoJson;
        return this;
    }

    @Override
    public String buildJsElement() {
        // all the options and methods are going to be implemented through the object as they are usually
        // provided using feature for each geojson object
        return String.format("""
                        let %1$s = L.geoJSON(%2$s, { %3$s });
                        this.%1$s = %1$s;
                        %1$s.uuid = '%1$s';
                        // callback start
                        %4$s
                        // callback end
                        %1$s.addTo(this.map);
                        """,
                getElementVarName(), geoJson, renderOptions(), renderCallbacks());

    }

    @Override
    public JLGeoJson buildJLObject() {
        return JLGeoJson.builder()
                .id(uuid)
                .geoJsonContent(geoJson)
                .transport(transporter)
                .build();
    }
}
