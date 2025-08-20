package io.github.makbn.jlmap.listener.event;

import io.github.makbn.jlmap.listener.JLAction;
import io.github.makbn.jlmap.model.JLLatLng;

/**
 * @author Matt Akbarian  (@makbn)
 */
public record ClickEvent(JLAction action, JLLatLng center) implements Event {
}
