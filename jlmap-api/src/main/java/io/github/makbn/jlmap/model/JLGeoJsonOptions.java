package io.github.makbn.jlmap.model;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Configuration options for GeoJSON layers with functional styling and filtering capabilities.
 * <p>
 * This class enables sophisticated GeoJSON layer customization through Java lambda functions
 * that are automatically proxied to JavaScript. It supports dynamic styling based on feature
 * properties and selective feature filtering.
 * </p>
 * <h3>Usage Examples:</h3>
 * <pre>{@code
 * // Style features based on type property
 * JLGeoJsonOptions options = JLGeoJsonOptions.builder()
 *     .styleFunction(features -> {
 *         String type = (String) features.get(0).get("type");
 *         return switch (type) {
 *             case "park" -> JLOptions.builder().fillColor(JLColor.GREEN).build();
 *             case "water" -> JLOptions.builder().fillColor(JLColor.BLUE).build();
 *             default -> JLOptions.DEFAULT;
 *         };
 *     })
 *     .filter(features -> {
 *         // Only show active features
 *         return Boolean.TRUE.equals(features.get(0).get("active"));
 *     })
 *     .build();
 *
 * // Apply to GeoJSON layer
 * JLGeoJson geoJson = map.getGeoJsonLayer().addFromUrl("data.geojson", options);
 * }</pre>
 * <p>
 * <strong>Function Parameters:</strong> Both {@code styleFunction} and {@code filter} receive
 * a {@code List<Map<String, Object>>} where {@code features.get(0)} contains the current
 * feature's properties as key-value pairs.
 * </p>
 *
 * @author Matt Akbarian (@makbn)
 * @since 2.0.0
 */
@Getter
@Setter
@Builder(toBuilder = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class JLGeoJsonOptions {

    /**
     * Function to dynamically style GeoJSON features based on their properties.
     * <p>
     * This function is called by Leaflet for each feature during rendering. The call is
     * automatically proxied back to this Java function, enabling type-safe styling logic.
     * </p>
     * <p>
     * <strong>Parameters:</strong> Receives a {@code List<Map<String, Object>>} where
     * {@code features.get(0)} contains the feature's properties map.
     * </p>
     * <p>
     * <strong>Return Value:</strong> Must return a {@link JLOptions} object defining
     * the visual styling (colors, opacity, stroke, etc.) for the feature.
     * </p>
     * <h4>Example:</h4>
     * <pre>{@code
     * styleFunction = features -> {
     *     Map<String, Object> properties = features.get(0);
     *     String landUse = (String) properties.get("landuse");
     *     return switch (landUse) {
     *         case "residential" -> JLOptions.builder().fillColor(JLColor.YELLOW).build();
     *         case "industrial" -> JLOptions.builder().fillColor(JLColor.GRAY).build();
     *         case "forest" -> JLOptions.builder().fillColor(JLColor.GREEN).build();
     *         default -> JLOptions.DEFAULT;
     *     };
     * };
     * }</pre>
     */
    Function<List<Map<String, Object>>, JLOptions> styleFunction;
    /**
     * Predicate to conditionally include or exclude GeoJSON features from display.
     * <p>
     * This function is called by Leaflet for each feature to determine visibility.
     * Features returning {@code false} will not be rendered on the map.
     * </p>
     * <p>
     * <strong>Parameters:</strong> Receives a {@code List<Map<String, Object>>} where
     * {@code features.get(0)} contains the feature's properties map.
     * </p>
     * <p>
     * <strong>Return Value:</strong> Must return {@code true} to show the feature,
     * or {@code false} to hide it.
     * </p>
     * <h4>Example:</h4>
     * <pre>{@code
     * filter = features -> {
     *     Map<String, Object> properties = features.get(0);
     *     Integer population = (Integer) properties.get("population");
     *     Boolean isActive = (Boolean) properties.get("active");
     *     // Show only active features with population > 10,000
     *     return Boolean.TRUE.equals(isActive) && population != null && population > 10000;
     * };
     * }</pre>
     */
    Predicate<List<Map<String, Object>>> filter;

    /**
     * Creates default GeoJSON options with no styling or filtering applied.
     * <p>
     * Features will be rendered with Leaflet's default styling and all features
     * will be visible. This is equivalent to {@code JLGeoJsonOptions.builder().build()}.
     * </p>
     *
     * @return a default {@link JLGeoJsonOptions} instance
     */
    public static JLGeoJsonOptions getDefault() {
        return JLGeoJsonOptions.builder().build();
    }
}