package io.github.makbn.jlmap.element.menu;

/**
 * Listener interface for handling context menu item selection events.
 * <p>
 * This listener is triggered when a user selects a specific menu item from a context menu.
 * It provides the selected menu item to the implementing code for further processing.
 * </p>
 * <h3>Usage Example:</h3>
 * <pre>{@code
 * JLContextMenu contextMenu = marker.getContextMenu();
 * contextMenu.setOnMenuItemListener(selectedItem -> {
 *     System.out.println("Selected: " + selectedItem.getText());
 *     if (selectedItem.getId().equals("delete")) {
 *         marker.remove();
 *     }
 * });
 * }</pre>
 *
 * @author Matt Akbarian (@makbn)
 * @since 2.0.0
 */
@FunctionalInterface
public interface OnJLContextMenuItemListener {

    /**
     * Called when a menu item is selected from the context menu.
     * <p>
     * This method is invoked whenever a user clicks on a menu item in the context menu.
     * The selected menu item is passed as a parameter, allowing the listener to
     * determine which action was chosen and respond accordingly.
     * </p>
     *
     * @param selectedItem the menu item that was selected by the user
     */
    void onMenuItemSelected(JLMenuItem selectedItem);
}