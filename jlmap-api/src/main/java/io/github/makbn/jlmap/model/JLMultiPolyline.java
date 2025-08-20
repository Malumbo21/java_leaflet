package io.github.makbn.jlmap.model;

import io.github.makbn.jlmap.engine.JLTransporter;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

/**
 * A class for drawing polyline overlays on a map
 *
 * @author Matt Akbarian  (@makbn)
 */
@Getter
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public final class JLMultiPolyline extends JLObject<JLMultiPolyline> {
    /**
     * id of JLMultiPolyline! this is an internal id for JLMap Application
     * and not related to Leaflet!
     */
    String id;
    /**
     * theming options for JLMultiPolyline. all options are not available!
     */
    JLOptions options;
    /**
     * The array of {@link io.github.makbn.jlmap.model.JLLatLng} points
     * of JLMultiPolyline
     */
    JLLatLng[][] vertices;

    @Builder
    public JLMultiPolyline(String id, JLOptions options, JLLatLng[][] vertices, JLTransporter transport) {
        super(transport);
        this.id = id;
        this.options = options;
        this.vertices = vertices;
    }
}
