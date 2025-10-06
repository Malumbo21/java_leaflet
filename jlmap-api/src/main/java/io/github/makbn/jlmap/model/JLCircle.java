package io.github.makbn.jlmap.model;

import io.github.makbn.jlmap.engine.JLServerToClientTransporter;
import io.github.makbn.jlmap.engine.JLTransportRequest;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.concurrent.CompletableFuture;

/**
 * Represents a circular vector overlay on the map with geographic radius.
 * <p>
 * A circle is defined by a center point ({@link JLLatLng}) and a radius in meters.
 * Unlike {@link JLCircleMarker}, which uses pixel-based radius, JLCircle maintains
 * its geographic size regardless of zoom level.
 * </p>
 * <p>
 * Circles can be styled, made interactive, and dynamically modified after creation.
 * They support all standard vector layer operations including click events, styling
 * changes, and geometric transformations.
 * </p>
 * <h3>Usage Examples:</h3>
 * <pre>{@code
 * // Create a basic circle
 * JLCircle circle = map.getVectorLayer().addCircle(
 *     new JLLatLng(40.7831, -73.9712), // New York City
 *     1000, // 1km radius
 *     JLOptions.builder().fillColor(JLColor.BLUE).fillOpacity(0.3).build()
 * );
 *
 * // Make it interactive
 * circle.setOnActionListener((circle, event) -> {
 *     if (event.action() == JLAction.CLICK) {
 *         circle.setRadius(circle.getRadius() * 1.5); // Expand on click
 *     }
 * });
 *
 * // Update properties dynamically
 * circle.setLatLng(new JLLatLng(40.7589, -73.9851)); // Move to Times Square
 * circle.setRadius(500); // Shrink to 500m
 * circle.setStyle(JLOptions.builder().fillColor(JLColor.RED).build());
 * }</pre>
 * <p>
 * <strong>Performance Note:</strong> Circles with very large radii may impact performance,
 * especially when many are displayed simultaneously.
 * </p>
 *
 * @author Matt Akbarian  (@makbn)
 * @see JLCircleMarker for pixel-based circular markers
 * @see JLOptions for styling options
 * @since 1.0.0
 */
@Getter
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
public final class JLCircle extends JLObjectBase<JLCircle> {

    /**
     * Geographic radius of the circle in meters.
     * <p>
     * This value represents the actual ground distance from the center to the edge.
     * The visual size of the circle will change as the user zooms in/out to maintain
     * this geographic accuracy.
     * </p>
     */
    double radius;

    /**
     * Center coordinates of the circle on the map.
     * <p>
     * Defines the geographic location (latitude/longitude) where the circle is centered.
     * Can be updated dynamically using {@link #setLatLng(JLLatLng)}.
     * </p>
     */
    JLLatLng latLng;

    /**
     * Visual styling options for the circle.
     * <p>
     * Controls appearance including fill color, stroke color, opacity, and other
     * visual properties. Not all {@link JLOptions} properties apply to circles.
     * See Leaflet documentation for circle-specific styling options.
     * </p>
     */
    JLOptions options;

    @Builder
    public JLCircle(String id, double radius, JLLatLng latLng, JLOptions options, JLServerToClientTransporter<?> transport) {
        super(id, transport);
        this.radius = radius;
        this.latLng = latLng;
        this.options = options;
    }

    /**
     * @inheritDoc
     */
    @Override
    public JLCircle self() {
        return this;
    }

    /**
     * Updates the radius of the circle with smooth animation.
     * <p>
     * Changes the geographic radius while maintaining the current center position.
     * The circle will visually resize on the map with animation.
     * </p>
     * <h4>Example:</h4>
     * <pre>{@code
     * circle.setRadius(2000); // Expand to 2km radius
     * }</pre>
     *
     * @param radius the new radius in meters (must be positive)
     * @return this circle instance for method chaining
     */
    @NonNull
    public JLCircle setRadius(double radius) {
        transport.execute(JLTransportRequest.voidCall(this, "setRadius", radius));
        this.radius = radius;
        return this;
    }

    /**
     * Converts the circle to GeoJSON format asynchronously.
     * <p>
     * Returns a {@link CompletableFuture} that will complete with the GeoJSON
     * string representation of this circle. The GeoJSON will include the circle's
     * geometry and any associated properties.
     * </p>
     * <h4>Example:</h4>
     * <pre>{@code
     * circle.toGeoJSON().thenAccept(geoJson -> {
     *     System.out.println("Circle GeoJSON: " + geoJson);
     *     // Process the GeoJSON string
     * });
     * }</pre>
     *
     * @return a {@link CompletableFuture} that will complete with the GeoJSON string
     */
    @NonNull
    public CompletableFuture<String> toGeoJSON() {
        return transport.execute(JLTransportRequest.returnableCall(this, "toGeoJSON", String.class));
    }

    /**
     * Moves the circle to a new geographic location with smooth animation.
     * <p>
     * Changes the center coordinates while maintaining the current radius and styling.
     * The circle will smoothly transition to the new position on the map.
     * </p>
     * <h4>Example:</h4>
     * <pre>{@code
     * // Move circle to London
     * circle.setLatLng(new JLLatLng(51.5074, -0.1278));
     * }</pre>
     *
     * @param latLng the new center coordinates (must not be null)
     * @return this circle instance for method chaining
     * @throws NullPointerException if latLng is null
     */
    @NonNull
    public JLCircle setLatLng(@NonNull JLLatLng latLng) {
        transport.execute(JLTransportRequest.voidCall(this, "setLatLng", latLng.toString()));
        this.latLng = latLng;
        return this;
    }

    /**
     * Calculates and returns the geographic bounds of the circle asynchronously.
     * <p>
     * Returns a {@link CompletableFuture} that will complete with a {@link JLBounds}
     * object representing the smallest rectangle that completely contains the circle.
     * This is useful for viewport calculations and spatial operations.
     * </p>
     * <h4>Example:</h4>
     * <pre>{@code
     * circle.getBounds().thenAccept(bounds -> {
     *     map.getControlLayer().fitBounds(bounds); // Zoom to fit the circle
     * });
     * }</pre>
     *
     * @return a {@link CompletableFuture} that will complete with the circle's bounds
     */
    @NonNull
    public CompletableFuture<JLBounds> getBounds() {
        return transport.execute(JLTransportRequest.returnableCall(this, "getBounds", JLBounds.class));
    }
}
