package io.github.makbn.jlmap.model;

import io.github.makbn.jlmap.engine.JLServerToClientTransporter;
import io.github.makbn.jlmap.engine.JLTransportRequest;
import lombok.*;
import lombok.experimental.FieldDefaults;

/**
 * An image overlay object given the URL of the image and the geographical bounds it is tied to.
 * * @author Matt Akbarian (@makbn)`
 */
@Getter
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
public final class JLImageOverlay extends JLObjectBase<JLImageOverlay> {

    /**
     * URL of the image to be used as an overlay. (can be local or remote URL)
     */
    String imageUrl;
    /**
     * Coordinates of the JLMarker on the map
     */
    JLBounds bounds;
    /**
     * theming options for JLImageOverlay. all options are not available!
     */
    JLOptions options;

    @Builder(toBuilder = true)
    private JLImageOverlay(String jLId, String imageUrl, JLBounds bounds, JLOptions options, JLServerToClientTransporter<?> transport) {
        super(jLId, transport);
        this.imageUrl = imageUrl;
        this.bounds = bounds;
        this.options = options;
    }


    @Override
    public JLImageOverlay self() {
        return this;
    }

    @NonNull
    public JLImageOverlay setBounds(@NonNull JLBounds bounds) {
        transport.execute(JLTransportRequest.voidCall(this, "setBounds", bounds.toString()));
        this.bounds = bounds;
        return this;
    }

    @NonNull
    public JLImageOverlay setUrl(@NonNull String imageUrl) {
        transport.execute(JLTransportRequest.voidCall(this, "setUrl", "'%s'" .formatted(imageUrl)));
        this.imageUrl = imageUrl;
        return this;
    }

    @NonNull
    public JLLatLng getCenter() {
        return bounds.getCenter();
    }
}
