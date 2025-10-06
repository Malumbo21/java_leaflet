package io.github.makbn.jlmap.model;

import io.github.makbn.jlmap.engine.JLServerToClientTransporter;
import io.github.makbn.jlmap.engine.JLTransportRequest;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.concurrent.CompletableFuture;

/**
 * A class for drawing polyline overlays on the map.
 *
 * @author Matt Akbarian  (@makbn)
 */
@Getter
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public final class JLPolyline extends JLObjectBase<JLPolyline> {
    /**
     * theming options for JLPolyline. all options are not available!
     */
    JLOptions options;
    /**
     * The array of {@link io.github.makbn.jlmap.model.JLLatLng} points of
     * JLPolyline
     */
    JLLatLng[] vertices;

    @Builder
    public JLPolyline(String id, JLOptions options, JLLatLng[] vertices, JLServerToClientTransporter<?> transport) {
        super(id, transport);
        this.options = options;
        this.vertices = vertices;
    }

    @Override
    public JLPolyline self() {
        return this;
    }

    /**
     * Converts the polyline to GeoJSON format asynchronously.
     * <p>
     * Returns a {@link CompletableFuture} that will complete with the GeoJSON
     * string representation of this polyline. The GeoJSON will include the polyline's
     * geometry and any associated properties.
     * </p>
     * <h4>Example:</h4>
     * <pre>{@code
     * polyline.toGeoJSON().thenAccept(geoJson -> {
     *     System.out.println("Polyline GeoJSON: " + geoJson);
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
