package io.github.makbn.jlmap.element.menu;

import io.github.makbn.jlmap.model.JLObject;
import lombok.NonNull;
import org.jetbrains.annotations.Nullable;

/**
 * Interface for JL objects that support context menu functionality.
 * <p>
 * This interface enables map elements (markers, polygons, polylines, etc.) to have
 * context menus that can be displayed when users right-click or long-press on them.
 * The context menu is implemented using platform-specific mediators to avoid
 * dependency on Leaflet's internal context menu implementation.
 * </p>
 * <h3>Context Menu Lifecycle:</h3>
 * <ul>
 *   <li><strong>Creation</strong>: Context menu is created when first accessed</li>
 *   <li><strong>Display</strong>: Menu shows on right-click/long-press events</li>
 *   <li><strong>Selection</strong>: User selects menu items triggering listeners</li>
 *   <li><strong>Closing</strong>: Menu closes automatically or by user action</li>
 * </ul>
 * <h3>Usage Example:</h3>
 * <pre>{@code
 * JLMarker marker = JLMarker.builder()
 *     .latLng(new JLLatLng(51.5, -0.09))
 *     .build();
 *
 * JLContextMenu contextMenu = marker.getContextMenu();
 * contextMenu.addItem("Edit", "edit-icon")
 *           .addItem("Delete", "delete-icon")
 *           .setOnMenuItemListener(item -> {
 *               switch (item.getId()) {
 *                   case "Edit" -> editMarker(marker);
 *                   case "Delete" -> marker.remove();
 *               }
 *           });
 * }</pre>
 *
 * @param <T> the type of the implementing JL object
 * @author Matt Akbarian (@makbn)
 * @since 2.0.0
 */
public interface JLHasContextMenu<T extends JLObject<T>> {

    /**
     * Retrieves the context menu for this JL object.
     * <p>
     * Returns the context menu associated with this object. If no context menu
     * has been created yet, a new one will be instantiated. The context menu
     * allows adding/removing menu items and setting up event listeners.
     * </p>
     * <p>
     * <strong>Note:</strong> The context menu is created lazily when first accessed.
     * This ensures optimal memory usage and performance for objects that may not
     * require context menu functionality.
     * </p>
     *
     * @return the context menu instance for this object, never null
     */
    @Nullable
    JLContextMenu<T> getContextMenu();

    void setContextMenu(@NonNull JLContextMenu<T> contextMenu);

    @NonNull
    JLContextMenu<T> addContextMenu();

    /**
     * Checks if this object has a context menu with visible items.
     * <p>
     * Returns true if a context menu exists and contains at least one menu item
     * that should be displayed to the user. This is useful for determining
     * whether to show context menu indicators or enable right-click functionality.
     * </p>
     * <h4>Implementation Note:</h4>
     * <p>
     * Objects should return false if:
     * </p>
     * <ul>
     *   <li>No context menu has been created</li>
     *   <li>Context menu exists but has no items</li>
     *   <li>All menu items are hidden or disabled</li>
     * </ul>
     *
     * @return true if the object has a visible context menu with items, false otherwise
     */
    boolean hasContextMenu();

    /**
     * Checks if the context menu is currently enabled for this object.
     * <p>
     * Returns true if the context menu will be displayed when users perform
     * context menu gestures on this object. A disabled context menu will not
     * appear regardless of whether it exists and contains menu items.
     * </p>
     *
     * @return true if the context menu is enabled, false otherwise
     */
    boolean isContextMenuEnabled();

    /**
     * Enables or disables the context menu for this object.
     * <p>
     * Controls whether the context menu should be displayed when users perform
     * context menu gestures (right-click, long-press, etc.) on this object.
     * When disabled, the context menu will not appear even if it exists and
     * contains menu items.
     * </p>
     * <p>
     * <strong>Default State:</strong> Context menus are enabled by default when created.
     * </p>
     * <h4>Use Cases:</h4>
     * <ul>
     *   <li>Temporarily disable context menu during editing operations</li>
     *   <li>Enable/disable based on user permissions or application state</li>
     *   <li>Provide conditional context menu availability</li>
     * </ul>
     *
     * @param enabled true to enable the context menu, false to disable it
     */
    void setContextMenuEnabled(boolean enabled);
}