package io.github.makbn.jlmap.vaadin.element.menu;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.contextmenu.ContextMenu;
import com.vaadin.flow.component.html.Image;
import io.github.makbn.jlmap.JLMap;
import io.github.makbn.jlmap.element.menu.JLContextMenuMediator;
import io.github.makbn.jlmap.element.menu.JLHasContextMenu;
import io.github.makbn.jlmap.model.JLObject;
import io.github.makbn.jlmap.vaadin.JLMapView;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;

/**
 * Vaadin-specific implementation of the context menu mediator.
 * <p>
 * This mediator handles context menu functionality for JL objects within
 * Vaadin applications. It creates and manages Vaadin ContextMenu components
 * and handles the integration between JL context menus and Vaadin's UI framework.
 * </p>
 * <h3>Features:</h3>
 * <ul>
 *   <li><strong>Native Integration</strong>: Uses Vaadin's ContextMenu component</li>
 *   <li><strong>Event Handling</strong>: Proper event delegation and lifecycle management</li>
 *   <li><strong>Component Mapping</strong>: Efficient mapping between JL objects and Vaadin components</li>
 *   <li><strong>Thread Safety</strong>: Safe for use in Vaadin's server-side architecture</li>
 * </ul>
 * <h3>Vaadin Integration:</h3>
 * <p>
 * This mediator integrates with Vaadin's component tree by:
 * </p>
 * <ul>
 *   <li>Finding the appropriate Vaadin component for each JL object</li>
 *   <li>Attaching ContextMenu to the component</li>
 *   <li>Handling right-click events through Vaadin's event system</li>
 *   <li>Managing component lifecycle and cleanup</li>
 * </ul>
 *
 * @author Matt Akbarian (@makbn)
 * @since 2.0.0
 */
@Slf4j
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class VaadinContextMenuMediator implements JLContextMenuMediator {
    public static final String ICON_SIZE = "14px";
    public static final String MARGIN_SIZE = "8px";
    ContextMenu universalContextMenu;

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized <T extends JLObject<T>> void showContextMenu(@NonNull JLMap<?> map, @NonNull JLObject<T> object, double x, double y) {
        log.debug("Showing context menu for object: {} at ({}, {})", object.getJLId(), x, y);
        if (universalContextMenu == null) {
            universalContextMenu = new ContextMenu((JLMapView) map);
        }
        if (object instanceof JLHasContextMenu<?> objectWithContextMenu
                && objectWithContextMenu.hasContextMenu() && objectWithContextMenu.isContextMenuEnabled()) {
            universalContextMenu.removeAll();
            Objects.requireNonNull(objectWithContextMenu.getContextMenu()).getItems().forEach(item -> {
                var menuItem = universalContextMenu.addItem(item.getText(), e ->
                        objectWithContextMenu.getContextMenu().getOnMenuItemListener().onMenuItemSelected(item));
                if (item.getIcon() != null && !item.getIcon().isBlank()) {
                    menuItem.addComponentAsFirst(createIcon(item.getIcon()));
                }
            });
        }
    }

    private Component createIcon(String url) {
        Image image = new Image(url, "");
        image.setWidth(ICON_SIZE);
        image.setHeight(ICON_SIZE);
        image.getStyle().setMarginRight(MARGIN_SIZE);

        return image;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized <T extends JLObject<T>> void hideContextMenu(@NonNull JLMap<?> map, @NonNull JLObject<T> object) {
        log.debug("Hiding context menu for object: {}", object.getJLId());
        if (universalContextMenu != null) {
            universalContextMenu.removeAll();
            universalContextMenu.removeFromParent();
            universalContextMenu.close();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean supportsObjectType(@NonNull Class<? extends JLObject<?>> objectType) {
        // Support all JL object types in Vaadin environment
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @NonNull
    public String getName() {
        return "Vaadin Context Menu Mediator";
    }
}