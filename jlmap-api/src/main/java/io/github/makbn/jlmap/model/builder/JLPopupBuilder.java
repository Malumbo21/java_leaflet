package io.github.makbn.jlmap.model.builder;

import io.github.makbn.jlmap.model.JLLatLng;
import io.github.makbn.jlmap.model.JLPopup;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.jetbrains.annotations.NotNull;

@FieldDefaults(level = AccessLevel.PRIVATE)
public class JLPopupBuilder extends JLObjectBuilder<JLPopup, JLPopupBuilder> {
    double lat;
    double lng;
    String content;

    public JLPopupBuilder setLat(double lat) {
        this.lat = lat;
        return this;
    }

    public JLPopupBuilder setLng(double lng) {
        this.lng = lng;
        return this;
    }

    public JLPopupBuilder setContent(String content) {
        this.content = content;
        return this;
    }

    @Override
    protected String getElementVarName() {
        return uuid;
    }


    @Override
    protected String getElementType() {
        return JLPopup.class.getSimpleName().toLowerCase();
    }

    @Override
    public String buildJsElement() {
        return String.format("""
                        let %1$s = L.popup({ %2$s })
                            .setLatLng([%3$f, %4$f])
                            .setContent(%5$s);
                        this.%1$s = %1$s;
                        %1$s.uuid = '%6$s';
                        // callback start
                        %7$s
                        // callback end
                        %1$s.addTo(this.map);
                        """,
                getElementVarName(),
                renderOptions(),
                lat, lng,
                sanitizeContent(),
                getElementVarName(),
                renderCallbacks());
    }

    private @NotNull String sanitizeContent() {
        return content != null ? "\"" + content.replace("\"", "\\\"") + "\"" : "\"\"";
    }

    @Override
    public JLPopup buildJLObject() {
        return JLPopup.builder()
                .id(uuid)
                .text(sanitizeContent())
                .latLng(JLLatLng.builder()
                        .lat(lat)
                        .lng(lng)
                        .build())
                .options(jlOptions)
                .transport(transporter)
                .build();
    }
}
