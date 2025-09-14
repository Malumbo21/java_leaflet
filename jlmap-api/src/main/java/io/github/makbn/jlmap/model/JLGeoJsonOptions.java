package io.github.makbn.jlmap.model;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Configuration options for GeoJSON layers with simple functional callbacks.
 * Leaflet will call these functions and proxy the call back to the Java object.
 *
 * @author Matt Akbarian (@makbn)
 */
@Getter
@Setter
@AllArgsConstructor
@Builder(toBuilder = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class JLGeoJsonOptions {

    /**
     * Default options for GeoJSON layers
     */
    public static final JLGeoJsonOptions DEFAULT = JLGeoJsonOptions.builder().build();


    /**
     * Style function that receives feature properties and returns styling options.
     * Called by Leaflet for each feature - the call is proxied back to this Java function.
     * Example: features -> JLOptions.builder().color(JLColor.RED).build()
     */
    Function<List<Map<String, Object>>, JLOptions> styleFunction;

    /**
     * Filter function that determines whether to include a feature.
     * Called by Leaflet for each feature - the call is proxied back to this Java function.
     * Example: features -> "park".equals(features.get("type"))
     */
    Predicate<List<Map<String, Object>>> filter;
}