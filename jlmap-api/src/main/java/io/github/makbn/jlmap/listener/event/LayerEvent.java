package io.github.makbn.jlmap.listener.event;

import io.github.makbn.jlmap.listener.JLAction;
import io.github.makbn.jlmap.model.JLBounds;
import io.github.makbn.jlmap.model.JLLatLng;

import java.util.List;

/**
 * @author Matt Akbarian  (@makbn)
 */
public record LayerEvent(JLAction action, List<List<JLLatLng>> latLng, JLBounds bounds) implements Event {
}
