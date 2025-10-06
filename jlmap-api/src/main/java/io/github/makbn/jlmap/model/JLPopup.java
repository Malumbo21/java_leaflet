package io.github.makbn.jlmap.model;

import io.github.makbn.jlmap.engine.JLServerToClientTransporter;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;

/**
 * Used to open popups in certain places of the map.
 *
 * @author Matt Akbarian  (@makbn)
 */
@Getter
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public final class JLPopup extends JLObjectBase<JLPopup> {
    /**
     * Content of the popup.
     */
    String text;
    /**
     * Coordinates of the popup on the map.
     */
    JLLatLng latLng;
    /**
     * Theming options for JLPopup. all options are not available!
     */
    JLOptions options;

    @Setter
    @NonFinal
    JLObject<?> parent;

    @Builder
    public JLPopup(String id, String text, JLLatLng latLng, JLOptions options, JLServerToClientTransporter<?> transport) {
        super(id, transport);
        this.text = text;
        this.latLng = latLng;
        this.options = options;
    }

    @Override
    public JLPopup self() {
        return this;
    }
}
