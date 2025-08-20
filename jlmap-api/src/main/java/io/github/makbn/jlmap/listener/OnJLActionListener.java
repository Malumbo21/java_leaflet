package io.github.makbn.jlmap.listener;

import io.github.makbn.jlmap.listener.event.Event;

/**
 * @author Matt Akbarian  (@makbn)
 */
public interface OnJLActionListener<T> {

     void onAction(T source, Event event);
}
