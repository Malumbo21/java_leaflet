package io.github.makbn.jlmap.model.builder;

import io.github.makbn.jlmap.model.JLBounds;
import io.github.makbn.jlmap.model.JLImageOverlay;
import io.github.makbn.jlmap.model.JLLatLng;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

import java.util.ArrayList;
import java.util.List;

@FieldDefaults(level = AccessLevel.PRIVATE)
public class JLImageOverlayBuilder extends JLObjectBuilder<JLImageOverlay, JLImageOverlayBuilder> {
    String imageUrl;
    List<double[]> bounds = new ArrayList<>();

    public JLImageOverlayBuilder setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
        return this;
    }

    /**
     * Set bounds using two double arrays: [lat, lng] for southwest and northeast corners.
     * First item is southwest and second is northeast.
     */
    public JLImageOverlayBuilder setBounds(List<double[]> bounds) {
        if (bounds == null || bounds.size() != 2)
            throw new IllegalArgumentException("Bounds must have exactly two coordinates (SW, NE)");
        this.bounds = bounds;
        return this;
    }

    @Override
    protected String getElementVarName() {
        return uuid;
    }

    @Override
    protected String getElementType() {
        return JLImageOverlay.class.getSimpleName().toLowerCase();
    }

    @Override
    public String buildJsElement() {
        if (imageUrl == null || bounds.size() != 2) return "";
        String boundsJs = String.format("[[%f, %f], [%f, %f]]", bounds.get(0)[0], bounds.get(0)[1], bounds.get(1)[0], bounds.get(1)[1]);
        return String.format("""
                            let %1$s = L.imageOverlay('%2$s', %3$s, { %4$s });
                            this.%1$s = %1$s;
                            %1$s.uuid = '%5$s';
                            // callback start
                            %6$s
                            // callback end
                            %1$s.addTo(this.map);
                        """,
                getElementVarName(),
                imageUrl,
                boundsJs,
                renderOptions(),
                getElementVarName(),
                renderCallbacks()
        );
    }

    @Override
    public JLImageOverlay buildJLObject() {
        // first element is southWest [lat, lng]
        var southWest = JLLatLng.builder()
                .lat(bounds.get(0)[0])
                .lng(bounds.get(0)[1])
                .build();

        // second element is northEast [lat, lng]
        var northEast = JLLatLng.builder()
                .lat(bounds.get(1)[0])
                .lng(bounds.get(1)[1])
                .build();

        var jlBounds = JLBounds.builder()
                .northEast(northEast)
                .southWest(southWest)
                .build();

        return JLImageOverlay.builder()
                .id(uuid)
                .imageUrl(imageUrl)
                .bounds(jlBounds)
                .options(jlOptions)
                .transport(transporter)
                .build();
    }
}
