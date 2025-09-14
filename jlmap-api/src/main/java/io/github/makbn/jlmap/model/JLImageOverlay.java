package io.github.makbn.jlmap.model;

import io.github.makbn.jlmap.engine.JLServerToClientTransporter;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

/**
 * An image overlay object given the URL of the image and the geographical bounds it is tied to.
 * * @author Matt Akbarian  (@makbn)`
 */
@Getter
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
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
}
