package io.github.makbn.jlmap.element.menu;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.NonNull;
import lombok.Value;
import lombok.experimental.FieldDefaults;

/**
 * Represents a single item in a context menu.
 * <p>
 * Each menu item has a unique identifier, display text, optional icon, and can be
 * enabled/disabled or shown/hidden. Menu items are immutable once created, promoting
 * consistent state management and thread safety.
 * </p>
 * <h3>Menu Item Properties:</h3>
 * <ul>
 *   <li><strong>ID</strong>: Unique identifier for programmatic access</li>
 *   <li><strong>Text</strong>: User-visible display text</li>
 *   <li><strong>Icon</strong>: Optional icon or image reference</li>
 *   <li><strong>Enabled</strong>: Whether the item can be selected</li>
 *   <li><strong>Visible</strong>: Whether the item appears in the menu</li>
 * </ul>
 * <h3>Usage Example:</h3>
 * <pre>{@code
 * JLMenuItem editItem = JLMenuItem.builder()
 *     .id("edit")
 *     .text("Edit Properties")
 *     .icon("edit-icon.png")
 *     .enabled(true)
 *     .visible(true)
 *     .build();
 *
 * JLMenuItem deleteItem = JLMenuItem.builder()
 *     .id("delete")
 *     .text("Delete Item")
 *     .icon("delete-icon.png")
 *     .enabled(canDelete)
 *     .visible(hasDeletePermission)
 *     .build();
 * }</pre>
 *
 * @author Matt Akbarian (@makbn)
 * @since 2.0.0
 */
@Value
@Builder(toBuilder = true)
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class JLMenuItem {

    /**
     * Unique identifier for this menu item.
     * <p>
     * The ID is used to identify the menu item when handling selection events
     * and for programmatic access. It should be unique within the context menu
     * and remain constant throughout the item's lifecycle.
     * </p>
     */
    @NonNull
    String id;

    /**
     * Display text shown to the user.
     * <p>
     * This is the human-readable text that appears in the context menu.
     * It should be descriptive and indicate the action that will be performed
     * when the menu item is selected.
     * </p>
     */
    @NonNull
    String text;

    /**
     * Optional icon or image reference for this menu item.
     * <p>
     * The icon can be a file path, URL, CSS class name, or any other identifier
     * that the underlying UI framework can interpret as an icon. The specific
     * format depends on the platform implementation (JavaFX, Vaadin, etc.).
     * </p>
     * <h4>Icon Format Examples:</h4>
     * <ul>
     *   <li><strong>File Path</strong>: "icons/edit.png"</li>
     *   <li><strong>CSS Class</strong>: "fa-edit" (FontAwesome)</li>
     *   <li><strong>URL</strong>: "https://example.com/icon.svg"</li>
     * </ul>
     */
    String icon;

    /**
     * Whether this menu item can be selected by the user.
     * <p>
     * Disabled items typically appear grayed out and do not respond to
     * click events. They remain visible but cannot be activated.
     * </p>
     * <p>
     * <strong>Default:</strong> true (enabled)
     * </p>
     */
    @Builder.Default
    boolean enabled = true;

    /**
     * Whether this menu item is visible in the context menu.
     * <p>
     * Hidden items do not appear in the menu at all. This is useful for
     * conditionally showing menu items based on user permissions, application
     * state, or other dynamic factors.
     * </p>
     * <p>
     * <strong>Default:</strong> true (visible)
     * </p>
     */
    @Builder.Default
    boolean visible = true;

    /**
     * Creates a simple menu item with just ID and text.
     * <p>
     * This is a convenience factory method for creating basic menu items
     * without icons or custom enabled/visible states.
     * </p>
     *
     * @param id   unique identifier for the menu item
     * @param text display text for the menu item
     * @return a new menu item instance
     */
    public static JLMenuItem of(@NonNull String id, @NonNull String text) {
        return JLMenuItem.builder()
                .id(id)
                .text(text)
                .build();
    }

    /**
     * Creates a menu item with ID, text, and icon.
     * <p>
     * This is a convenience factory method for creating menu items with
     * the most commonly used properties.
     * </p>
     *
     * @param id   unique identifier for the menu item
     * @param text display text for the menu item
     * @param icon icon or image reference for the menu item
     * @return a new menu item instance
     */
    public static JLMenuItem of(@NonNull String id, @NonNull String text, String icon) {
        return JLMenuItem.builder()
                .id(id)
                .text(text)
                .icon(icon)
                .build();
    }
}
