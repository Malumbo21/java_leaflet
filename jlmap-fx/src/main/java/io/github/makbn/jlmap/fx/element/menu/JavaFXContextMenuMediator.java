package io.github.makbn.jlmap.fx.element.menu;

import io.github.makbn.jlmap.JLMap;
import io.github.makbn.jlmap.element.menu.JLContextMenuMediator;
import io.github.makbn.jlmap.element.menu.JLHasContextMenu;
import io.github.makbn.jlmap.fx.JLMapView;
import io.github.makbn.jlmap.model.JLObject;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;

/**
 * JavaFX-specific implementation of the context menu mediator.
 * <p>
 * This mediator handles context menu functionality for JL objects within
 * JavaFX applications. It creates and manages JavaFX ContextMenu components
 * and handles the integration between JL context menus and JavaFX's UI framework.
 * </p>
 * <h3>Features:</h3>
 * <ul>
 *   <li><strong>Native Integration</strong>: Uses JavaFX's ContextMenu component</li>
 *   <li><strong>Event Handling</strong>: Proper event delegation and lifecycle management</li>
 *   <li><strong>WebView Integration</strong>: Specialized handling for map content in WebView</li>
 *   <li><strong>Thread Safety</strong>: Safe for use in JavaFX's single-threaded architecture</li>
 * </ul>
 * <h3>JavaFX Integration:</h3>
 * <p>
 * This mediator integrates with JavaFX's scene graph by:
 * </p>
 * <ul>
 *   <li>Finding the appropriate JavaFX node for each JL object</li>
 *   <li>Attaching mouse event handlers to detect right-click</li>
 *   <li>Creating and managing ContextMenu instances</li>
 *   <li>Handling menu item clicks and event propagation</li>
 * </ul>
 *
 * @author Matt Akbarian (@makbn)
 * @since 2.0.0
 */
@Slf4j
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class JavaFXContextMenuMediator implements JLContextMenuMediator {
    public static final double ICON_SIZE = 14.0;
    ContextMenu universalContextMenu;
    boolean mouseHandlerRegistered = false;
    long lastMenuShowTime = 0;

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized <T extends JLObject<T>> void showContextMenu(@NonNull JLMap<?> map, @NonNull JLObject<T> object, double x, double y) {
        log.debug("Showing context menu for object: {} at ({}, {})", object.getJLId(), x, y);
        ensureContextMenuInitialized();

        if (!(object instanceof JLHasContextMenu<?> objectWithContextMenu)) {
            return;
        }

        logContextMenuDebugInfo(objectWithContextMenu);

        if (shouldShowContextMenu(objectWithContextMenu)) {
            populateMenuItems(objectWithContextMenu);
            displayContextMenu(map, x, y);
        }
    }

    private void ensureContextMenuInitialized() {
        if (universalContextMenu == null) {
            universalContextMenu = new ContextMenu();
        }
    }

    private void logContextMenuDebugInfo(JLHasContextMenu<?> objectWithContextMenu) {
        log.debug("Object is JLHasContextMenu: true");
        log.debug("Has context menu: {}, Enabled: {}", objectWithContextMenu.hasContextMenu(), objectWithContextMenu.isContextMenuEnabled());
        log.debug("Context menu object: {}", objectWithContextMenu.getContextMenu());
        if (objectWithContextMenu.getContextMenu() != null) {
            log.debug("Context menu items count: {}", objectWithContextMenu.getContextMenu().getItemCount());
        }
    }

    private boolean shouldShowContextMenu(JLHasContextMenu<?> objectWithContextMenu) {
        return objectWithContextMenu.hasContextMenu() && objectWithContextMenu.isContextMenuEnabled();
    }

    private void populateMenuItems(JLHasContextMenu<?> objectWithContextMenu) {
        universalContextMenu.getItems().clear();
        Objects.requireNonNull(objectWithContextMenu.getContextMenu()).getItems().forEach(item -> {
            log.debug("Adding menu item: {}", item.getText());
            MenuItem menuItem = createMenuItem(item, objectWithContextMenu);
            universalContextMenu.getItems().add(menuItem);
        });
    }

    private MenuItem createMenuItem(io.github.makbn.jlmap.element.menu.JLMenuItem item, JLHasContextMenu<?> objectWithContextMenu) {
        MenuItem menuItem = new MenuItem(item.getText());
        menuItem.setOnAction(e ->
                Objects.requireNonNull(objectWithContextMenu.getContextMenu()).getOnMenuItemListener().onMenuItemSelected(item));
        if (item.getIcon() != null && !item.getIcon().isBlank()) {
            menuItem.setGraphic(createIcon(item.getIcon()));
        }
        return menuItem;
    }

    private void displayContextMenu(@NonNull JLMap<?> map, double x, double y) {
        if (!(map instanceof JLMapView jlMapView)) {
            log.warn("Map is not a JLMapView: {}", map.getClass());
            return;
        }

        log.debug("Showing context menu with {} items at WebView-relative position ({}, {})",
                universalContextMenu.getItems().size(), x, y);

        registerClickHandlerIfNeeded(jlMapView);
        showMenuAtPosition(jlMapView, x, y);
    }

    private void registerClickHandlerIfNeeded(JLMapView jlMapView) {
        if (mouseHandlerRegistered) {
            return;
        }

        jlMapView.getWebView().setOnMouseClicked(event -> {
            long timeSinceShow = System.currentTimeMillis() - lastMenuShowTime;
            if (universalContextMenu != null && universalContextMenu.isShowing() && timeSinceShow > 100) {
                log.debug("Hiding context menu due to WebView click (time since show: {}ms)", timeSinceShow);
                universalContextMenu.hide();
            }
        });
        mouseHandlerRegistered = true;
    }

    private void showMenuAtPosition(JLMapView jlMapView, double x, double y) {
        javafx.geometry.Point2D screenCoords = jlMapView.getWebView().localToScreen(x, y);
        if (screenCoords != null) {
            log.debug("Screen coordinates: ({}, {})", screenCoords.getX(), screenCoords.getY());
            lastMenuShowTime = System.currentTimeMillis();
            universalContextMenu.show(jlMapView.getWebView(), screenCoords.getX(), screenCoords.getY());
        } else {
            log.warn("Could not convert coordinates to screen space");
        }
    }

    private ImageView createIcon(String url) {
        Image image = new Image(url);
        ImageView imageView = new ImageView(image);
        imageView.setFitWidth(ICON_SIZE);
        imageView.setFitHeight(ICON_SIZE);
        imageView.setPreserveRatio(true);
        return imageView;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized <T extends JLObject<T>> void hideContextMenu(@NonNull JLMap<?> map, @NonNull JLObject<T> object) {
        log.debug("Hiding context menu for object: {}", object.getJLId());
        if (universalContextMenu != null) {
            universalContextMenu.hide();
            universalContextMenu.getItems().clear();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean supportsObjectType(@NonNull Class<? extends JLObject<?>> objectType) {
        // Support all JL object types in JavaFX environment
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @NonNull
    public String getName() {
        return "JavaFX Context Menu Mediator";
    }
}