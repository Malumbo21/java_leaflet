package io.github.makbn.jlmap.listener.event;

import io.github.makbn.jlmap.listener.JLAction;
import io.github.makbn.jlmap.model.JLBounds;
import io.github.makbn.jlmap.model.JLLatLng;

/**
 * @author Matt Akbarian  (@makbn)
 */
public record LayerEvent(JLAction action, JLLatLng center, JLBounds bounds) implements Event{
}
