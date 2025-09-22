package io.github.makbn.jlmap.model;

import io.github.makbn.jlmap.engine.JLServerToClientTransporter;
import io.github.makbn.jlmap.engine.JLTransportRequest;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.concurrent.CompletableFuture;

/**
 * A class for drawing polygon overlays on the map.
 * Note that points you pass when creating a polygon shouldn't
 * have an additional last point equal to the first one.
 *
 * @author Matt Akbarian  (@makbn)
 */
@Getter
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public final class JLPolygon extends JLObjectBase<JLPolygon> {

    /**
     * theming options for JLMultiPolyline. all options are not available!
     */
    JLOptions options;

    /**
     * The arrays of lat-lng, with the first array representing the outer
     * shape and the other arrays representing holes in the outer shape.
     * Additionally, you can pass a multidimensional array to represent
     * a MultiPolygon shape.
     */
    JLLatLng[][][] vertices;

    @Builder
    public JLPolygon(String id, JLOptions options, JLLatLng[][][] vertices, JLServerToClientTransporter<?> transport) {
        super(id, transport);
        this.options = options;
        this.vertices = vertices;
    }

    @Override
    public JLPolygon self() {
        return this;
    }

    /**
     * Converts the polygon to GeoJSON format asynchronously.
     * <p>
     * Returns a {@link CompletableFuture} that will complete with the GeoJSON
     * string representation of this polygon. The GeoJSON will include the polygon's
     * geometry and any associated properties.
     * </p>
     * <h4>Example:</h4>
     * <pre>{@code
     * polygon.toGeoJSON().thenAccept(geoJson -> {
     *     System.out.println("Polygon GeoJSON: " + geoJson);
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

}
