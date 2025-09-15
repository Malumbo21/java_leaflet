package io.github.makbn.jlmap.listener.event;

import io.github.makbn.jlmap.listener.JLAction;
import io.github.makbn.jlmap.model.JLBounds;
import io.github.makbn.jlmap.model.JLLatLng;

import java.util.List;

/**
 * Represents an event related to a map layer, such as adding or removing a layer.
 * <p>
 * This event is used in {@link JLLayerEventHandler} to signal when a layer is added or removed from the map.
 * The event contains:
 * <ul>
 *   <li><b>action</b>: The {@link JLAction} performed (ADD or REMOVE).</li>
 *   <li><b>latLng</b>: The coordinates of the layer, as a list of lists of {@link JLLatLng} (for polygons, polylines, etc.).</li>
 *   <li><b>bounds</b>: The {@link JLBounds} of the layer after the action.</li>
 * </ul>
 * <p>
 * Usage in {@link JLLayerEventHandler}:
 * <ul>
 *   <li>When a layer is added, a LayerEvent is fired with JLAction.ADD, the layer's coordinates, and bounds.</li>
 *   <li>When a layer is removed, a LayerEvent is fired with JLAction.REMOVE, the layer's coordinates, and bounds.</li>
 * </ul>
 * <p>
 * This event allows listeners to react to layer changes, such as updating UI, analytics, or custom logic.
 * </p>
 * @author Matt Akbarian  (@makbn)
 */
public record LayerEvent(JLAction action, List<List<JLLatLng>> latLng, JLBounds bounds) implements Event {
}
