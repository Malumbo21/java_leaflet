package io.github.makbn.jlmap.listener.event;

import io.github.makbn.jlmap.listener.JLAction;
import io.github.makbn.jlmap.listener.OnJLMapViewListener;
import io.github.makbn.jlmap.model.JLBounds;
import io.github.makbn.jlmap.model.JLLatLng;

/**
 *
 * @param action    movement action
 * @param center    coordinate of map
 * @param bounds    bounds of map
 * @param zoomLevel zoom level of map
 *
 *  @author Matt Akbarian  (@makbn)
 *
 */
public record DragEvent(JLAction action, JLLatLng center,
                        JLBounds bounds, int zoomLevel) implements Event {
}
