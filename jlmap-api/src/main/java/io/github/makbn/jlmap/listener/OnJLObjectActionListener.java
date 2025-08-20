package io.github.makbn.jlmap.listener;

import io.github.makbn.jlmap.listener.event.ClickEvent;
import io.github.makbn.jlmap.listener.event.Event;
import io.github.makbn.jlmap.listener.event.MoveEvent;
import io.github.makbn.jlmap.model.JLObject;

/**
 * @author Matt Akbarian  (@makbn)
 */
public interface OnJLObjectActionListener<T extends JLObject<?>> extends OnJLActionListener<T> {

    /**
     * try to override and use {@link #onAction(JLObject, Event)}
     * @deprecated this method will be removed with the next minor version
     */
    @Deprecated(forRemoval = true)
    default void click(T t, JLAction action) {
        // NO-OP
    }

    /**
     * try to override and use {@link #onAction(JLObject, Event)}
     * @deprecated this method will be removed with the next minor version
     */
    @Deprecated(forRemoval = true)
    default void move(T t, JLAction action) {
        // NO-OP
    }

    /**
     * try to override and use {@link #onAction(JLObject, Event)}
     * @deprecated this method added for the transition period and will be removed with the next minor version
     */
    @Deprecated(forRemoval = true)
    void onActionReceived(T t, Event event);

    @Override
    default void onAction(T source, Event event) {
        onActionReceived(source, event);
        if (event instanceof MoveEvent moveEvent) {
            move(source, moveEvent.action());
        } else if (event instanceof ClickEvent clickEvent) {
            click(source, clickEvent.action());
        }
    }
}
