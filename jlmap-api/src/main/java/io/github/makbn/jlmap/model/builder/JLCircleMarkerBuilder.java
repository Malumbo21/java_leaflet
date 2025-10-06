package io.github.makbn.jlmap.model.builder;

import io.github.makbn.jlmap.model.JLCircleMarker;
import io.github.makbn.jlmap.model.JLLatLng;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

/**
 * @author Matt Akbarian  (@makbn)
 */
@FieldDefaults(level = AccessLevel.PRIVATE)
public class JLCircleMarkerBuilder extends JLObjectBuilder<JLCircleMarker, JLCircleMarkerBuilder> {
    double lat;
    double lng;
    double radius = 10; // default Leaflet radius

    public JLCircleMarkerBuilder setLat(double lat) {
        this.lat = lat;
        return this;
    }

    public JLCircleMarkerBuilder setLng(double lng) {
        this.lng = lng;
        return this;
    }

    public JLCircleMarkerBuilder setRadius(double radius) {
        this.radius = radius;
        return this;
    }

    @Override
    protected String getElementVarName() {
        return uuid;
    }

    @Override
    protected String getElementType() {
        return JLCircleMarker.class.getSimpleName().toLowerCase();
    }

    @Override
    public String buildJsElement() {
        return String.format("""
                        let %1$s = L.circleMarker([%2$f, %3$f], { "radius": %4$f, %5$s });
                        this.%1$s = %1$s;
                        %1$s.uuid = '%6$s';
                        // callback start
                        %7$s
                        // callback end
                        %1$s.addTo(this.map);
                        """,
                getElementVarName(),
                lat, lng,
                radius,
                renderOptions(),
                getElementVarName(),
                renderCallbacks());
    }

    @Override
    public JLCircleMarker buildJLObject() {
        return JLCircleMarker.builder()
                .id(uuid)
                .latLng(JLLatLng.builder()
                        .lng(lng)
                        .lat(lat)
                        .build())
                .options(jlOptions)
                .radius(radius)
                .transport(transporter)
                .build();
    }

}
