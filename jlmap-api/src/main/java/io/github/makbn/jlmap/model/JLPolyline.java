package io.github.makbn.jlmap.model;

import io.github.makbn.jlmap.engine.JLTransporter;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

/**
 * A class for drawing polyline overlays on the map.
 *
 * @author Matt Akbarian  (@makbn)
 */
@Getter
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public final class JLPolyline extends JLObject<JLPolyline> {
    /**
     * id of JLPolyline! this is an internal id for JLMap Application and not
     * related to Leaflet!
     */
    String id;
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
    public JLPolyline(String id, JLOptions options, JLLatLng[] vertices, JLTransporter transport) {
        super(transport);
        this.id = id;
        this.options = options;
        this.vertices = vertices;
    }
}
