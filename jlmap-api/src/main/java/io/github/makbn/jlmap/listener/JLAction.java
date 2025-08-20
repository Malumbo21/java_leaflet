package io.github.makbn.jlmap.listener;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

/**
 * @author Matt Akbarian  (@makbn)
 */
@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public enum JLAction {
    /**
     * Zoom level changes continuously
     */
    ZOOM("zoom"),
    /**
     * Zoom level stats to change
     */
    ZOOM_START("zoomstart"),
    /**
     * Zoom leve changes end
     */
    ZOOM_END("zoomend"),

    /**
     * Element is being moved
     */
    MOVE("move"),
    /**
     * User starts to move the element
     */
    MOVE_START("movestart"),
    /**
     * User ends to move the layer
     */
    MOVE_END("moveend"),

    /**
     * The element is being dragged
     */
    DRAG("drag"),
    /**
     * User starts to drag
     */
    DRAG_START("dragstart"),
    /**
     * User drag ends
     */
    DRAG_END("dragend"),
    /**
     * User click on the layer
     */
    CLICK("click"),
    /**
     * User double-clicks (or double-taps) the layer.
     */
    DOUBLE_CLICK("dblclick"),
    /**
     * Fired after the layer is added to a map
     */
    ADD("add"),
    /**
     * Fired after the layer is removed from a map
     */
    REMOVE("remove"),
    /**
     * Fired when the map is resized.
     */
    RESIZE("resize");

    String jsEventName;
}
