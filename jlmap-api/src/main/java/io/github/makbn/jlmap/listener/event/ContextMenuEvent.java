package io.github.makbn.jlmap.listener.event;

import io.github.makbn.jlmap.listener.JLAction;
import io.github.makbn.jlmap.model.JLBounds;
import io.github.makbn.jlmap.model.JLLatLng;

/**
 * Event representing context menu lifecycle actions (open/close).
 * <p>
 * This event is fired when a context menu is opened, allowing
 * listeners to respond to context menu state changes. The event includes
 * the coordinates where the menu was triggered and the type of action.
 *
 * @author Matt Akbarian (@makbn)
 * @since 2.0.0
 */
public record ContextMenuEvent(JLAction action, JLLatLng latLng, JLBounds jlBounds, double x,
                               double y) implements Event {

    public ContextMenuEvent(JLAction action, JLLatLng latLng, JLBounds jlBounds, double... position) {
        this(action, latLng, jlBounds,
                position != null && position.length > 0 ? position[0] : 0D,
                position != null && position.length > 1 ? position[1] : 0D);
    }
}