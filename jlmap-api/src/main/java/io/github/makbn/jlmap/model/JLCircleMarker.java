package io.github.makbn.jlmap.model;

import io.github.makbn.jlmap.engine.JLServerToClientTransporter;
import io.github.makbn.jlmap.engine.JLTransportRequest;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.concurrent.CompletableFuture;

/**
 * A circle of a fixed size with radius specified in pixels.
 *
 * @author Matt Akbarian  (@makbn)
 */
@Getter
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
public final class JLCircleMarker extends JLObjectBase<JLCircleMarker> {
    /**
     * Radius of the circle marker, in pixels
     */
    double radius;
    /**
     * Coordinates of the JLCircleMarker on the map
     */
    JLLatLng latLng;
    /**
     * theming options for JLCircleMarker. all options are not available!
     */
    JLOptions options;

    @Builder
    public JLCircleMarker(String id, double radius, JLLatLng latLng, JLOptions options, JLServerToClientTransporter<?> transport) {
        super(id, transport);
        this.radius = radius;
        this.latLng = latLng;
        this.options = options;
    }

    @Override
    public JLCircleMarker self() {
        return this;
    }

    /**
     * Converts the circle marker to GeoJSON format asynchronously.
     * <p>
     * Returns a {@link CompletableFuture} that will complete with the GeoJSON
     * string representation of this circle. The GeoJSON will include the circle's
     * geometry and any associated properties.
     * </p>
     * <h4>Example:</h4>
     * <pre>{@code
     * circleMarker.toGeoJSON().thenAccept(geoJson -> {
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
     * Moves the circle to a new geographic location.
     * <p>
     * Changes the center coordinates while maintaining the current radius and styling.
     * The circle will smoothly transition to the new position on the map.
     * </p>
     * <h4>Example:</h4>
     * <pre>{@code
     * // Move circle to London
     * circleMarker.setLatLng(new JLLatLng(51.5074, -0.1278));
     * }</pre>
     *
     * @param latLng the new center coordinates (must not be null)
     * @return this circle instance for method chaining
     * @throws NullPointerException if latLng is null
     */
    @NonNull
    public JLCircleMarker setLatLng(@NonNull JLLatLng latLng) {
        transport.execute(JLTransportRequest.voidCall(this, "setLatLng", latLng.toString()));
        this.latLng = latLng;
        return this;
    }

    /**
     * Sets the radius of the circleMarker.
     * <p>
     * Changes the geographic radius while maintaining the current center position.
     * The circle will visually resize on the map with animation.
     * </p>
     * <h4>Example:</h4>
     * <pre>{@code
     * circleMarker.setRadius(2000);
     * }</pre>
     *
     * @param radius the new radius in pixels (must be positive)
     * @return this circle instance for method chaining
     */
    @NonNull
    public JLCircleMarker setRadius(double radius) {
        transport.execute(JLTransportRequest.voidCall(this, "setRadius", radius));
        this.radius = radius;
        return this;
    }


}
