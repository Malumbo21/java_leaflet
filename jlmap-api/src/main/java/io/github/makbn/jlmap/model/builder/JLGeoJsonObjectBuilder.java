package io.github.makbn.jlmap.model.builder;

import io.github.makbn.jlmap.engine.JLClientToServerTransporter;
import io.github.makbn.jlmap.model.JLGeoJson;
import io.github.makbn.jlmap.model.JLGeoJsonOptions;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

import java.util.ArrayList;
import java.util.List;

@FieldDefaults(level = AccessLevel.PRIVATE)
public class JLGeoJsonObjectBuilder extends JLObjectBuilder<JLGeoJson, JLGeoJsonObjectBuilder> {
    String geoJson;
    JLGeoJsonOptions geoJsonOptions;
    JLClientToServerTransporter serverToClient;

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

    public JLGeoJsonObjectBuilder withGeoJsonOptions(JLGeoJsonOptions geoJsonOptions) {
        this.geoJsonOptions = geoJsonOptions;
        return this;
    }

    public JLGeoJsonObjectBuilder withBridge(JLClientToServerTransporter bridge) {
        this.serverToClient = bridge;
        return this;
    }

    @Override
    public String buildJsElement() {
        return String.format("""
                        let %1$s = L.geoJSON(%2$s, { %3$s });
                        this.%1$s = %1$s;
                        %1$s.uuid = '%1$s';
                        // callback start
                        %4$s
                        // callback end
                        %1$s.addTo(this.map);
                        """,
                getElementVarName(), geoJson, renderGeoJsonOptions(), renderCallbacks());
    }

    private String renderGeoJsonOptions() {
        List<String> optionParts = new ArrayList<>();

        // Add base style options if no custom style function is provided
        if (geoJsonOptions == null || geoJsonOptions.getStyleFunction() == null) {
            String baseOptions = renderOptions();
            if (!baseOptions.isEmpty()) {
                optionParts.add(baseOptions);
            }
        }

        if (geoJsonOptions != null) {
            // Add style function callback using bridge
            if (geoJsonOptions.getStyleFunction() != null) {
                //language=js
                optionParts.add("""
                        onEachFeature: function(feature, layer) {
                            window.jlObjectBridge.call('%1$s', 'callFilterFunction', JSON.stringify(feature)).then(filterResult => {
                                if (!filterResult || filterResult === 'false') {
                                    layer.remove();
                                } else {
                                     window.jlObjectBridge.call('%1$s', 'callStyleFunction', JSON.stringify(feature.properties)).then(styleResult => {
                                        console.log(styleResult);
                                        layer.setStyle(styleResult ? JSON.parse(styleResult) : {});
                                    });
                                }
                            });
                        }
                        """.formatted(uuid));
            }

            // Add filter function callback using bridge
            if (geoJsonOptions.getFilter() != null) {
                optionParts.add("filter: function(feature, layer) { " +
                        "var result = window.jlObjectBridge.call('" + uuid + "', 'callFilterFunction', JSON.stringify(feature)); " +
                        "return true; " +
                        "}");
            }

        }

        return String.join(", ", optionParts);
    }

    @Override
    public JLGeoJson buildJLObject() {
        JLGeoJson geoJsonObject = JLGeoJson.builder()
                .id(uuid)
                .geoJsonContent(geoJson)
                .geoJsonOptions(geoJsonOptions)
                .transport(transporter)
                .build();

        serverToClient.registerObject(uuid, geoJsonObject);

        return geoJsonObject;
    }
}
