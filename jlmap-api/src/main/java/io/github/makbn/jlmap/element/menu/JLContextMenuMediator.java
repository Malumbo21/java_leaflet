package io.github.makbn.jlmap.element.menu;

import io.github.makbn.jlmap.JLMap;
import io.github.makbn.jlmap.model.JLObject;
import lombok.NonNull;

/**
 * Platform-independent mediator interface for context menu implementations.
 * <p>
 * This interface abstracts the platform-specific context menu implementations
 * (JavaFX, Vaadin, etc.) from the common JL context menu API. Each UI framework
 * should provide its own implementation of this mediator to handle platform-specific
 * menu rendering, event handling, and user interactions.
 * </p>
 * <h3>Mediator Pattern Benefits:</h3>
 * <ul>
 *   <li><strong>Platform Independence</strong>: JL objects work with any UI framework</li>
 *   <li><strong>Separation of Concerns</strong>: Business logic separate from UI rendering</li>
 *   <li><strong>Extensibility</strong>: Easy to add support for new UI frameworks</li>
 *   <li><strong>Testability</strong>: Can provide mock implementations for testing</li>
 * </ul>
 * <h3>Implementation Requirements:</h3>
 * <p>
 * Concrete mediators must:
 * </p>
 * <ul>
 *   <li>Render context menus using platform-specific UI components</li>
 *   <li>Handle user interaction events (right-click, long-press, etc.)</li>
 *   <li>Forward menu item selections to the appropriate listeners</li>
 *   <li>Manage menu lifecycle (show, hide, update) efficiently</li>
 *   <li>Be thread-safe for multithreaded environments</li>
 * </ul>
 * <h3>Service Provider Interface:</h3>
 * <p>
 * Implementations should be discoverable via Java's ServiceLoader mechanism
 * by providing a service configuration file in META-INF/services/.
 * </p>
 * <h4>Example Service Configuration:</h4>
 * <pre>
 * File: META-INF/services/io.github.makbn.jlmap.element.menu.JLContextMenuMediator
 * Content: io.github.makbn.jlmap.vaadin.menu.VaadinContextMenuMediator
 * </pre>
 *
 * @author Matt Akbarian (@makbn)
 * @since 2.0.0
 */
public interface JLContextMenuMediator {


    /**
     * Shows the context menu for the specified JL object at the given coordinates.
     * <p>
     * This method is typically called in response to user gestures like right-click
     * or long-press. The mediator should display the context menu using platform-specific
     * popup or menu components at the specified screen coordinates.
     * </p>
     * <p>
     * <strong>Coordinate System:</strong> Coordinates are typically relative to
     * the map view or screen, depending on the platform implementation.
     * </p>
     *
     * @param <T>    the type of JL object
     * @param object the JL object whose context menu should be shown
     * @param x      the x-coordinate where the menu should appear
     * @param y      the y-coordinate where the menu should appear
     * @throws IllegalArgumentException if object is null
     * @throws IllegalStateException    if no context menu is registered for the object
     */
    <T extends JLObject<T>> void showContextMenu(@NonNull JLMap<?> map, @NonNull JLObject<T> object, double x, double y);

    /**
     * Hides the context menu for the specified JL object.
     * <p>
     * This method is called when the context menu should be dismissed, either
     * programmatically or in response to user actions like clicking outside
     * the menu area.
     * </p>
     *
     * @param <T>    the type of JL object
     * @param object the JL object whose context menu should be hidden
     * @throws IllegalArgumentException if object is null
     */
    <T extends JLObject<T>> void hideContextMenu(@NonNull JLMap<?> map, @NonNull JLObject<T> object);

    /**
     * Checks if the mediator supports the specified JL object type.
     * <p>
     * This method allows the service locator to determine which mediator
     * should handle a particular JL object. Mediators may support all object
     * types or be specialized for specific object categories.
     * </p>
     * <h4>Example Use Cases:</h4>
     * <ul>
     *   <li>General mediators support all JLObject types</li>
     *   <li>Specialized mediators only support markers or polygons</li>
     *   <li>Framework-specific mediators support objects within their UI context</li>
     * </ul>
     *
     * @param objectType the class type of the JL object
     * @return true if this mediator can handle the specified object type
     * @throws IllegalArgumentException if objectType is null
     */
    boolean supportsObjectType(@NonNull Class<? extends JLObject<?>> objectType);

    /**
     * Returns a human-readable name for this mediator implementation.
     * <p>
     * This method is useful for debugging, logging, and development tools
     * that need to identify which mediator is being used.
     * </p>
     * <h4>Naming Convention:</h4>
     * <p>
     * Names should include the target platform or framework:
     * </p>
     * <ul>
     *   <li>"Vaadin Context Menu Mediator"</li>
     *   <li>"JavaFX Context Menu Mediator"</li>
     *   <li>"Mock Context Menu Mediator"</li>
     * </ul>
     *
     * @return a descriptive name for this mediator
     */
    @NonNull
    String getName();
}