package io.github.makbn.jlmap.model.builder;

import io.github.makbn.jlmap.model.JLLatLng;
import io.github.makbn.jlmap.model.JLMarker;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE)
public class JLMarkerBuilder extends JLObjectBuilder<JLMarker, JLMarkerBuilder> {
    double lat;
    double lng;
    String text;

    public JLMarkerBuilder setLat(double lat) {
        this.lat = lat;
        return this;
    }

    public JLMarkerBuilder setLng(double lng) {
        this.lng = lng;
        return this;
    }

    public JLMarkerBuilder setText(String text) {
        this.text = text;
        return this;
    }

    @Override
    protected String getElementVarName() {
        return uuid;
    }

    @Override
    protected String getElementType() {
        return JLMarker.class.getSimpleName().toLowerCase();
    }

    @Override
    public String buildJsElement() {
        return String.format("""
                        let %1$s = L.marker([%2$f, %3$f], { %4$s });
                        this.%1$s = %1$s;
                        %1$s.uuid = '%5$s';
                        // callback start
                        %6$s
                        // callback end
                        %1$s.addTo(this.map);
                        """,
                getElementVarName(), lat, lng,
                renderOptions(), getElementVarName(),
                renderCallbacks());
    }

    @Override
    public JLMarker buildJLObject() {
        return JLMarker.builder()
                .id(getElementVarName())
                .latLng(JLLatLng.builder()
                        .lat(lat)
                        .lng(lng)
                        .build())
                .text(text)
                .transport(transporter)
                .build();
    }
}
