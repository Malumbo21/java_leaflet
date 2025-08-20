package io.github.makbn.jlmap.model.builder;

import io.github.makbn.jlmap.model.JLCircle;
import io.github.makbn.jlmap.model.JLLatLng;
import io.github.makbn.jlmap.model.JLOptions;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

/**
 * @author Matt Akbarian  (@makbn)
 */
@FieldDefaults(level = AccessLevel.PRIVATE)
public class JLCircleBuilder extends JLObjectBuilder<JLCircle, JLCircleBuilder> {
    double lat;
    double lng;
    double radius;

    public JLCircleBuilder setLat(double lat) {
        this.lat = lat;
        return this;
    }

    public JLCircleBuilder setLng(double lng) {
        this.lng = lng;
        return this;
    }

    public JLCircleBuilder setRadius(double r) {
        this.radius = r;
        return this;
    }

    @Override
    protected String getElementVarName() {
        return uuid;
    }

    @Override
    protected String getElementType() {
        return JLCircle.class.getSimpleName().toLowerCase();
    }

    @Override
    public String buildJsElement() {
        return String.format("""
                        let %1$s = L.circle([%2$f, %3$f], { radius: %4$f, %5$s });
                        this.%1$s = %1$s;
                        %1$s.uuid = '%6$s';
                        // callback start
                        %7$s
                        // callback end
                        %1$s.addTo(this.map);
                        """,
                getElementVarName(), lat, lng, radius,
                renderOptions(), getElementVarName(),
                renderCallbacks());
    }

    @Override
    public JLCircle buildJLObject() {
        return JLCircle.builder()
                .id(getElementVarName())
                .radius(radius)
                .latLng(JLLatLng.builder()
                        .lat(lat)
                        .lng(lng)
                        .build())
                .options(JLOptions.DEFAULT)
                .transport(transporter)
                .build();
    }
}
