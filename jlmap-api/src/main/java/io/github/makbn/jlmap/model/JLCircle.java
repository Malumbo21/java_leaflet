package io.github.makbn.jlmap.model;

import io.github.makbn.jlmap.engine.JLServerToClientTransporter;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

/**
 * A class for drawing circle overlays on a map
 *
 * @author Matt Akbarian  (@makbn)
 */
@Getter
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public final class JLCircle extends JLObjectBase<JLCircle> {

    /**
     * Radius of the circle, in meters.
     */
    double radius;
    /**
     * Coordinates of the JLMarker on the map
     */
    JLLatLng latLng;
    /**
     * theming options for JLCircle. all options are not available!
     */
    JLOptions options;

    @Builder
    public JLCircle(String id, double radius, JLLatLng latLng, JLOptions options, JLServerToClientTransporter<?> transport) {
        super(id, transport);
        this.radius = radius;
        this.latLng = latLng;
        this.options = options;
    }

    @Override
    public JLCircle self() {
        return this;
    }
}
