package io.github.makbn.jlmap.listener;

import io.github.makbn.jlmap.listener.event.ClickEvent;
import io.github.makbn.jlmap.listener.event.Event;
import io.github.makbn.jlmap.listener.event.MoveEvent;
import io.github.makbn.jlmap.model.JLObject;
import lombok.Getter;


public interface OnJLObjectActionListener<T extends JLObject<?>> extends OnJLActionListener<T> {

    @Deprecated(forRemoval = true)
    default void click(T t, JLAction action) {
        // NO-OP
    }

    @Deprecated(forRemoval = true)
    default void move(T t, JLAction action) {
        // NO-OP
    }

    /**
     * try to override and use {@link #onAction(JLObject, Event)}
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
