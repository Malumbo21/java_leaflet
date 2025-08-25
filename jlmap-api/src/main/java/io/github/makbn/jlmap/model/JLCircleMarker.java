package io.github.makbn.jlmap.model;

import io.github.makbn.jlmap.engine.JLTransporter;
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
public final class JLCircleMarker extends JLObject<JLCircleMarker> {
    /**
     * id of object! this is an internal id for JLMap Application and not
     * related to Leaflet!
     */
    String id;
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
    public JLCircleMarker(String id, double radius, JLLatLng latLng, JLOptions options, JLTransporter<?> transport) {
        super(transport);
        this.id = id;
        this.radius = radius;
        this.latLng = latLng;
        this.options = options;
    }

    @Override
    public JLCircleMarker self() {
        return this;
    }
}
