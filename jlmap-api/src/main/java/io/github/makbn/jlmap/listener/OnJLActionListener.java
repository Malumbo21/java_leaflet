package io.github.makbn.jlmap.listener;

import io.github.makbn.jlmap.listener.event.Event;

public interface OnJLActionListener<T> {

     void onAction(T source, Event event);
}
