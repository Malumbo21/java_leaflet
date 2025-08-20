package io.github.makbn.jlmap.listener.event;

import io.github.makbn.jlmap.listener.JLAction;

/**
 * @author Matt Akbarian  (@makbn)
 */
public record ZoomEvent(JLAction action, int zoomLevel)
        implements Event {
}
