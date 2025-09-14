package io.github.makbn.jlmap.listener.event;

import io.github.makbn.jlmap.listener.JLAction;
import io.github.makbn.jlmap.model.JLBounds;

/**
 * @author Matt Akbarian  (@makbn)
 */
public record ZoomEvent(JLAction action, int zoomLevel, JLBounds bounds) implements Event {

}
