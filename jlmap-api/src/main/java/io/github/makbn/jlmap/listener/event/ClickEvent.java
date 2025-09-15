package io.github.makbn.jlmap.listener.event;

import io.github.makbn.jlmap.listener.JLAction;
import io.github.makbn.jlmap.model.JLLatLng;

/**
 * Represents a click or double-click event on a map layer or object, analogous to Leaflet's
 * <a href="https://leafletjs.com/reference.html#map-click">click</a> and
 * <a href="https://leafletjs.com/reference.html#map-dblclick">dblclick</a> events.
 * <p>
 * This event is fired when the user clicks or double-clicks a map layer or object. It contains information about
 * the action (CLICK or DOUBLE_CLICK) and the geographic coordinates where the event occurred.
 * </p>
 * <ul>
 *   <li><b>action</b>: The {@link JLAction} performed (CLICK or DOUBLE_CLICK).</li>
 *   <li><b>center</b>: The geographic coordinates ({@link JLLatLng}) where the event occurred.</li>
 * </ul>
 * <p>
 * Usage in {@link JLInteractionEventHandler}:
 * <ul>
 *   <li>When a layer is clicked, a ClickEvent is fired with JLAction.CLICK and the click coordinates.</li>
 *   <li>When a layer is double-clicked, a ClickEvent is fired with JLAction.DOUBLE_CLICK and the coordinates.</li>
 * </ul>
 * <p>
 * This event allows listeners to react to user interactions, such as showing popups, selecting features, or triggering custom logic.
 * </p>
 * @see <a href="https://leafletjs.com/reference.html#map-click">Leaflet click event</a>
 * @see <a href="https://leafletjs.com/reference.html#map-dblclick">Leaflet dblclick event</a>
 *
 * @author Matt Akbarian  (@makbn)
 */
public record ClickEvent(JLAction action, JLLatLng center) implements Event {
}
