package io.github.makbn.jlmap.model;

import io.github.makbn.jlmap.engine.JLTransporter;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

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
public final class JLPolygon extends JLObject<JLPolygon> {
    /**
     * id of JLPolygon! this is an internal id for JLMap Application and not
     * related to Leaflet!
     */
    String id;
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
    public JLPolygon(String id, JLOptions options, JLLatLng[][][] vertices, JLTransporter<?> transport) {
        super(transport);
        this.id = id;
        this.options = options;
        this.vertices = vertices;
    }

    @Override
    public JLPolygon self() {
        return this;
    }
}
