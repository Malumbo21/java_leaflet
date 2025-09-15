package io.github.makbn.jlmap.listener.event;

import io.github.makbn.jlmap.listener.JLAction;

/**
 * Base interface for all map interaction events in JLMap.
 * <p>
 * Subclasses of {@code Event} represent specific types of user or programmatic interactions with the map.
 * Each event contains a {@link JLAction} describing the type of action performed.
 * </p>
 * <p>
 * <b>Known subclasses:</b>
 * <ul>
 *   <li>{@link MoveEvent} – Fired when the map is moved (see <a href="https://leafletjs.com/reference.html#map-move">Leaflet move events</a>).</li>
 *   <li>{@link ResizeEvent} – Fired when the map container is resized (<a href="https://leafletjs.com/reference.html#resizeevent">Leaflet resize event</a>).</li>
 *   <li>{@link ZoomEvent} – Fired when the map zoom level changes (<a href="https://leafletjs.com/reference.html#map-zoom">Leaflet zoom event</a>).</li>
 *   <li>{@link LayerEvent} – Fired when a layer is added or removed.</li>
 *   <li>{@link DragEvent} – Fired when a marker or object is dragged (<a href="https://leafletjs.com/reference.html#marker-drag">Leaflet drag events</a>).</li>
 *   <li>{@link ClickEvent} – Fired when a map layer or object is clicked or double-clicked (<a href="https://leafletjs.com/reference.html#map-click">Leaflet click events</a>).</li>
 * </ul>
 * <p>
 * Implementations of this interface are used in event handlers to provide detailed context about map interactions.
 * </p>
 * @author Matt Akbarian  (@makbn)
 */
public interface Event {
    JLAction action();
}
