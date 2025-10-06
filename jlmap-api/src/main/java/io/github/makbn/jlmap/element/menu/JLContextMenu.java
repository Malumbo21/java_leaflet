package io.github.makbn.jlmap.element.menu;

import io.github.makbn.jlmap.listener.OnJLActionListener;
import io.github.makbn.jlmap.listener.event.Event;
import io.github.makbn.jlmap.model.JLObject;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Represents a context menu for JL map objects.
 * <p>
 * The context menu provides a platform-independent way to add interactive menus
 * to map elements. It uses mediators to handle platform-specific implementations
 * while maintaining a consistent API across different UI frameworks (JavaFX, Vaadin).
 * </p>
 * <h3>Key Features:</h3>
 * <ul>
 *   <li><strong>Platform Independence</strong>: Uses mediators for framework-specific rendering</li>
 *   <li><strong>Dynamic Menu Items</strong>: Add, remove, and modify menu items at runtime</li>
 *   <li><strong>Event Handling</strong>: Supports both menu lifecycle and item selection events</li>
 *   <li><strong>Thread Safety</strong>: Uses concurrent collections for safe multi-threaded access</li>
 * </ul>
 * <h3>Event Flow:</h3>
 * <ol>
 *   <li><strong>Context Menu Opens</strong>: OnJLActionListener receives open event</li>
 *   <li><strong>User Selects Item</strong>: OnJLContextMenuItemListener receives selection</li>
 *   <li><strong>Context Menu Closes</strong>: OnJLActionListener receives close event</li>
 * </ol>
 * <h3>Usage Example:</h3>
 * <pre>{@code
 * JLMarker marker = JLMarker.builder()
 *     .latLng(new JLLatLng(51.5, -0.09))
 *     .build();
 *
 * JLContextMenu<JLMarker> menu = marker.getContextMenu();
 * menu.addItem("Edit", "edit-icon")
 *     .addItem("Delete", "delete-icon")
 *     .addItem("Info", "info-icon")
 *     .setOnMenuItemListener(item -> {
 *         switch (item.getId()) {
 *             case "Edit" -> editMarker(marker);
 *             case "Delete" -> deleteMarker(marker);
 *             case "Info" -> showMarkerInfo(marker);
 *         }
 *     });
 * }</pre>
 *
 * @param <T> the type of JL object this context menu belongs to
 * @author Matt Akbarian (@makbn)
 * @since 2.0.0
 */
@FieldDefaults(level = AccessLevel.PRIVATE)
public class JLContextMenu<T extends JLObject<T>> {

    /**
     * The JL object that owns this context menu.
     */
    @NonNull
    final T owner;

    /**
     * Thread-safe map of menu items indexed by their IDs.
     * Uses ConcurrentHashMap to support safe concurrent access during
     * menu operations and item modifications.
     */
    @NonNull
    final Map<String, JLMenuItem> menuItems = new ConcurrentHashMap<>();

    /**
     * Listener for menu item selection events.
     * This listener is called when a user selects a specific menu item.
     */
    @Getter
    OnJLContextMenuItemListener onMenuItemListener;

    /**
     * Whether the context menu is currently enabled.
     * Disabled menus will not be displayed even if they contain items.
     */
    @Getter
    @Setter
    boolean enabled = true;

    /**
     * Creates a new context menu for the specified owner object.
     *
     * @param owner the JL object that will own this context menu
     */
    public JLContextMenu(@NonNull T owner) {
        this.owner = owner;
    }

    /**
     * Adds a menu item with the specified text.
     * <p>
     * Creates a new menu item with the given text and an auto-generated ID.
     * The ID will be derived from the text by removing spaces and converting
     * to lowercase for consistent identification.
     * </p>
     *
     * @param text the display text for the menu item
     * @return this context menu instance for method chaining
     * @throws IllegalArgumentException if text is null or empty
     */
    @NonNull
    public JLContextMenu<T> addItem(@NonNull String text) {
        String id = text.replaceAll("\\s+", "").toLowerCase();
        return addItem(id, text);
    }

    /**
     * Adds a menu item with the specified ID and text.
     * <p>
     * Creates a new menu item with the given ID and display text.
     * If a menu item with the same ID already exists, it will be replaced.
     * </p>
     *
     * @param id   unique identifier for the menu item
     * @param text display text for the menu item
     * @return this context menu instance for method chaining
     * @throws IllegalArgumentException if id or text is null or empty
     */
    @NonNull
    public JLContextMenu<T> addItem(@NonNull String id, @NonNull String text) {
        JLMenuItem item = JLMenuItem.of(id, text);
        menuItems.put(id, item);
        notifyMenuUpdated();
        return this;
    }

    /**
     * Adds a menu item with the specified ID, text, and icon.
     * <p>
     * Creates a new menu item with all primary properties specified.
     * If a menu item with the same ID already exists, it will be replaced.
     * </p>
     *
     * @param id   unique identifier for the menu item
     * @param text display text for the menu item
     * @param icon icon or image reference for the menu item
     * @return this context menu instance for method chaining
     * @throws IllegalArgumentException if id or text is null or empty
     */
    @NonNull
    public JLContextMenu<T> addItem(@NonNull String id, @NonNull String text, String icon) {
        JLMenuItem item = JLMenuItem.of(id, text, icon);
        menuItems.put(id, item);
        notifyMenuUpdated();
        return this;
    }

    /**
     * Adds a pre-built menu item to the context menu.
     * <p>
     * Allows adding fully configured menu items with custom properties.
     * If a menu item with the same ID already exists, it will be replaced.
     * </p>
     *
     * @param item the menu item to add
     * @return this context menu instance for method chaining
     * @throws IllegalArgumentException if item is null
     */
    @NonNull
    public JLContextMenu<T> addItem(@NonNull JLMenuItem item) {
        menuItems.put(item.getId(), item);
        notifyMenuUpdated();
        return this;
    }

    /**
     * Removes a menu item with the specified ID.
     * <p>
     * If no menu item exists with the given ID, this method has no effect.
     * </p>
     *
     * @param id the ID of the menu item to remove
     * @return this context menu instance for method chaining
     */
    @NonNull
    public JLContextMenu<T> removeItem(@NonNull String id) {
        if (menuItems.remove(id) != null) {
            notifyMenuUpdated();
        }
        return this;
    }

    /**
     * Removes all menu items from the context menu.
     * <p>
     * After calling this method, the context menu will be empty and
     * will not be displayed until new items are added.
     * </p>
     *
     * @return this context menu instance for method chaining
     */
    @NonNull
    public JLContextMenu<T> clearItems() {
        if (!menuItems.isEmpty()) {
            menuItems.clear();
            notifyMenuUpdated();
        }
        return this;
    }

    /**
     * Retrieves a menu item by its ID.
     * <p>
     * Returns the menu item with the specified ID, or null if no such
     * item exists in the context menu.
     * </p>
     *
     * @param id the ID of the menu item to retrieve
     * @return the menu item with the specified ID, or null if not found
     */
    public JLMenuItem getItem(@NonNull String id) {
        return menuItems.get(id);
    }

    /**
     * Updates an existing menu item with new properties.
     * <p>
     * Finds the menu item with the matching ID and replaces it with the
     * updated version. If no item exists with the given ID, the new item
     * will be added to the menu.
     * </p>
     *
     * @param updatedItem the updated menu item
     * @return this context menu instance for method chaining
     * @throws IllegalArgumentException if updatedItem is null
     */
    @NonNull
    public JLContextMenu<T> updateItem(@NonNull JLMenuItem updatedItem) {
        menuItems.put(updatedItem.getId(), updatedItem);
        notifyMenuUpdated();
        return this;
    }

    /**
     * Returns an unmodifiable collection of all menu items.
     * <p>
     * The returned collection reflects the current state of the menu items
     * and will be updated as items are added or removed. However, the
     * collection itself cannot be modified directly.
     * </p>
     *
     * @return an unmodifiable collection of all menu items
     */
    @NonNull
    public Collection<JLMenuItem> getItems() {
        return Collections.unmodifiableCollection(menuItems.values());
    }

    /**
     * Returns an unmodifiable collection of all visible menu items.
     * <p>
     * Filters the menu items to include only those that are marked as visible.
     * This is useful for determining what items should actually be displayed
     * to the user.
     * </p>
     *
     * @return an unmodifiable collection of visible menu items
     */
    @NonNull
    public Collection<JLMenuItem> getVisibleItems() {
        return menuItems.values().stream()
                .filter(JLMenuItem::isVisible)
                .toList();
    }

    /**
     * Checks if the context menu has any visible items.
     * <p>
     * Returns true if there is at least one menu item that is marked as visible.
     * This is useful for determining whether the context menu should be displayed.
     * </p>
     *
     * @return true if there are visible menu items, false otherwise
     */
    public boolean hasVisibleItems() {
        return menuItems.values().stream().anyMatch(JLMenuItem::isVisible);
    }

    /**
     * Returns the number of menu items in the context menu.
     * <p>
     * Includes both visible and hidden items in the count.
     * </p>
     *
     * @return the total number of menu items
     */
    public int getItemCount() {
        return menuItems.size();
    }

    /**
     * Returns the number of visible menu items in the context menu.
     * <p>
     * Counts only the items that are marked as visible.
     * </p>
     *
     * @return the number of visible menu items
     */
    public int getVisibleItemCount() {
        return (int) menuItems.values().stream().filter(JLMenuItem::isVisible).count();
    }

    /**
     * Sets the listener for menu item selection events.
     * <p>
     * The listener will be called whenever a user selects a menu item from
     * the context menu. Only one listener can be active at a time; setting
     * a new listener will replace the previous one.
     * </p>
     *
     * @param listener the listener for menu item selection events, or null to remove
     * @return this context menu instance for method chaining
     */
    @NonNull
    public JLContextMenu<T> setOnMenuItemListener(OnJLContextMenuItemListener listener) {
        this.onMenuItemListener = listener;
        return this;
    }

    /**
     * Handles menu item selection events.
     * <p>
     * This method is called internally when a menu item is selected.
     * It delegates to the registered menu item listener if one exists.
     * </p>
     * <p>
     * <strong>Internal API:</strong> This method is intended for use by
     * the context menu mediator implementations and should not be called
     * directly by application code.
     * </p>
     *
     * @param selectedItem the menu item that was selected
     */
    public void handleMenuItemSelection(@NonNull JLMenuItem selectedItem) {
        if (onMenuItemListener != null) {
            onMenuItemListener.onMenuItemSelected(selectedItem);
        }
    }

    /**
     * Handles context menu lifecycle events.
     * <p>
     * This method is called when the context menu is opened or closed,
     * allowing the owner object to respond to these events through its
     * OnJLActionListener.
     * </p>
     * <p>
     * <strong>Internal API:</strong> This method is intended for use by
     * the context menu mediator implementations and should not be called
     * directly by application code.
     * </p>
     *
     * @param event the context menu event (open/close)
     */
    public void handleContextMenuEvent(@NonNull Event event) {
        OnJLActionListener<T> listener = owner.getOnActionListener();
        if (listener != null) {
            listener.onAction(owner, event);
        }
    }

    /**
     * Notifies that the menu structure has been updated.
     * <p>
     * This method is called internally whenever menu items are added, removed,
     * or modified. It can be used by mediator implementations to refresh
     * the display or update platform-specific menu representations.
     * </p>
     * <p>
     * <strong>Internal API:</strong> This method is intended for use by
     * the context menu mediator implementations and should not be called
     * directly by application code.
     * </p>
     */
    protected void notifyMenuUpdated() {
        // This method can be overridden by mediator implementations
        // to respond to menu changes
    }
}
