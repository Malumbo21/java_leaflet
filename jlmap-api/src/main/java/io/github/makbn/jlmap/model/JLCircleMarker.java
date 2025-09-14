package io.github.makbn.jlmap.model;

import io.github.makbn.jlmap.engine.JLServerToClientTransporter;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

/**
 * A circle of a fixed size with radius specified in pixels.
 *
 * @author Matt Akbarian  (@makbn)
 */
@Getter
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
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
}
