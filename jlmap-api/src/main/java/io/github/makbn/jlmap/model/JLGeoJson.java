package io.github.makbn.jlmap.model;

import io.github.makbn.jlmap.engine.JLServerToClientTransporter;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

import java.util.List;
import java.util.Map;

/**
 * Represents a GeoJSON layer in a Leaflet map.
 * Supports advanced styling, filtering, and interaction options.
 *
 * @author Matt Akbarian (@makbn)
 */
@Getter
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public final class JLGeoJson extends JLObjectBase<JLGeoJson> {
    /**
     * GeoJSON content as a string
     */
    String geoJsonContent;
    /**
     * the styling and configuration options for this GeoJSON layer.
     */
    JLGeoJsonOptions geoJsonOptions;

    @Builder
    public JLGeoJson(String id, String geoJsonContent, JLGeoJsonOptions geoJsonOptions, JLServerToClientTransporter<?> transport) {
        super(id, transport);
        this.geoJsonContent = geoJsonContent;
        this.geoJsonOptions = geoJsonOptions != null ? geoJsonOptions : JLGeoJsonOptions.getDefault();
    }

    @Override
    public JLGeoJson self() {
        return this;
    }

    /**
     * Calls the style function for a feature. This is called by the JavaScript callback.
     *
     * @param featureProperties The feature properties from Leaflet
     * @return The styling options
     */
    public JLOptions callStyleFunction(List<Map<String, Object>> featureProperties) {
        if (geoJsonOptions != null && geoJsonOptions.getStyleFunction() != null) {
            return geoJsonOptions.getStyleFunction().apply(featureProperties);
        }
        return JLOptions.DEFAULT;
    }

    /**
     * Calls the filter function for a feature. This is called by the JavaScript callback.
     *
     * @param featureProperties The feature properties from Leaflet
     * @return true if the feature should be included
     */
    public boolean callFilterFunction(List<Map<String, Object>> featureProperties) {
        if (geoJsonOptions != null && geoJsonOptions.getFilter() != null) {
            return geoJsonOptions.getFilter().test(featureProperties);
        }
        return true;
    }
}
