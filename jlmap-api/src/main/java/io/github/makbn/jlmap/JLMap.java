package io.github.makbn.jlmap;

import io.github.makbn.jlmap.engine.JLServerToClientTransporter;
import io.github.makbn.jlmap.engine.JLWebEngine;
import io.github.makbn.jlmap.exception.JLMapNotReadyException;
import io.github.makbn.jlmap.layer.leaflet.*;
import io.github.makbn.jlmap.model.JLLatLng;
import io.github.makbn.jlmap.model.JLObject;
import lombok.NonNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

/**
 * Core interface representing a Java Leaflet map instance with unified API across UI frameworks.
 * <p>
 * This interface provides a consistent abstraction layer for map operations in both JavaFX and Vaadin
 * implementations, allowing developers to write framework-agnostic mapping code.
 * </p>
 * <p>
 * The interface provides access to various layers (UI, Vector, Control, GeoJSON) and map manipulation
 * methods such as view setting, zooming, and retrieving map state information.
 * </p>
 *
 * @param <T> The framework-specific type for web engine operations
 * @author Matt Akbarian  (@makbn)
 * @since 2.0.0
 */
public interface JLMap<T> extends JLObject<JLMap<T>> {

    /**
     * Returns the underlying web engine used for JavaScript execution and map rendering.
     * <p>
     * The web engine handles the communication between Java code and the Leaflet JavaScript library,
     * enabling dynamic map operations and event handling.
     * </p>
     *
     * @return the web engine instance specific to the UI framework implementation
     */
    JLWebEngine<T> getJLEngine();

    /**
     * Initializes and adds the JavaScript map controller to the document.
     * <p>
     * This method sets up the necessary JavaScript infrastructure for map operations,
     * including event handlers and communication bridges between Java and JavaScript layers.
     * </p>
     * <p>
     * Typically called internally during map initialization and should not be invoked manually.
     * </p>
     */
    void addControllerToDocument();

    /**
     * Returns the internal registry of map layers by their class types.
     * <p>
     * This method provides access to the underlying layer management system, mapping
     * layer interface classes to their concrete implementations.
     * </p>
     * <p>
     * <strong>Note:</strong> This is primarily for internal use. Access layers through
     * the dedicated getter methods instead: {@link #getUiLayer()}, {@link #getVectorLayer()},
     * {@link #getControlLayer()}, {@link #getGeoJsonLayer()}.
     * </p>
     *
     * @return a map of layer classes to their instances
     */
    HashMap<Class<? extends LeafletLayer>, LeafletLayer> getLayers();

    /**
     * Provides access to the UI layer for managing markers, popups, and overlays.
     * <p>
     * The UI layer handles user interface elements that appear above the map content,
     * including markers with custom icons, popups with HTML content, and image overlays.
     * </p>
     *
     * @return the UI layer interface for adding and managing user interface elements
     * @throws JLMapNotReadyException if the map is not properly initialized
     */
    default LeafletUILayerInt getUiLayer() {
        checkMapState();
        return getLayerInternal(LeafletUILayerInt.class);
    }

    /**
     * Provides access to the vector layer for managing geometric shapes and paths.
     * <p>
     * The vector layer handles geometric elements such as circles, polygons, polylines,
     * and other vectorial shapes that can be styled and made interactive.
     * </p>
     *
     * @return the vector layer interface for adding and managing geometric shapes
     * @throws JLMapNotReadyException if the map is not properly initialized
     */
    default LeafletVectorLayerInt getVectorLayer() {
        checkMapState();
        return getLayerInternal(LeafletVectorLayerInt.class);
    }

    /**
     * Provides access to the control layer for map navigation and view management.
     * <p>
     * The control layer handles map manipulation operations such as zooming, panning,
     * setting bounds, and controlling the map's viewport and navigation state.
     * </p>
     *
     * @return the control layer interface for map navigation and view control
     * @throws JLMapNotReadyException if the map is not properly initialized
     */
    default LeafletControlLayerInt getControlLayer() {
        checkMapState();
        return getLayerInternal(LeafletControlLayerInt.class);
    }

    /**
     * Provides access to the GeoJSON layer for managing geographic data layers.
     * <p>
     * The GeoJSON layer handles the loading, styling, and interaction with GeoJSON
     * geographic data, supporting both simple displays and advanced features like
     * custom styling functions and data filtering.
     * </p>
     *
     * @return the GeoJSON layer interface for managing geographic data
     * @throws JLMapNotReadyException if the map is not properly initialized
     */
    default LeafletGeoJsonLayerInt getGeoJsonLayer() {
        checkMapState();
        return getLayerInternal(LeafletGeoJsonLayerInt.class);
    }


    /**
     * Smoothly pans the map view to the specified geographical coordinates.
     * <p>
     * This method performs an animated transition to center the map on the given location
     * while maintaining the current zoom level. The pan operation uses the default
     * animation duration and easing settings.
     * </p>
     *
     * @param latLng the target geographical coordinates to pan to
     * @throws JLMapNotReadyException if the map is not properly initialized
     */
    default void setView(JLLatLng latLng) {
        checkMapState();
        getJLEngine()
                .executeScript(String.format("this.map.panTo([%f, %f]);",
                        latLng.getLat(), latLng.getLng()));
    }

    /**
     * Smoothly pans the map view to the specified geographical coordinates with custom animation duration.
     * <p>
     * This method performs an animated transition to center the map on the given location
     * while maintaining the current zoom level, using the specified animation duration.
     * </p>
     *
     * @param latLng   the target geographical coordinates to pan to
     * @param duration the animation duration in milliseconds
     * @throws JLMapNotReadyException if the map is not properly initialized
     */
    default void setView(JLLatLng latLng, int duration) {
        checkMapState();
        getJLEngine()
                .executeScript(String.format("this.map.panTo([%f, %f], %d);",
                        latLng.getLat(), latLng.getLng(), duration));
    }

    /**
     * Retrieves the current zoom level of the map.
     * <p>
     * Zoom levels typically range from 0 (world view) to 18+ (building level detail),
     * depending on the tile provider. Each zoom level represents a doubling of the scale.
     * </p>
     *
     * @return the current zoom level as an integer
     * @throws JLMapNotReadyException if the map is not properly initialized
     */
    default int getZoom() {
        checkMapState();
        Object result = getJLEngine()
                .executeScript("this.map.getZoom();");
        return Integer.parseInt(result.toString());
    }

    /**
     * Sets the zoom level of the map with animation.
     * <p>
     * This method smoothly animates the map to the specified zoom level.
     * Zoom levels typically range from 0 (world view) to 18+ (building level detail).
     * Values outside the supported range will be clamped to valid bounds.
     * </p>
     *
     * @param zoomLevel the target zoom level (typically 0-19)
     * @throws JLMapNotReadyException if the map is not properly initialized
     */
    default void setZoom(int zoomLevel) {
        checkMapState();
        getJLEngine()
                .executeScript(String.format("this.map.setZoom(%d);", zoomLevel));
    }

    /**
     * Retrieves the current center coordinates of the map view.
     * <p>
     * This method returns the geographical coordinates (latitude and longitude)
     * that correspond to the center point of the current map viewport.
     * </p>
     *
     * @return the center coordinates as a {@link JLLatLng} object
     * @throws JLMapNotReadyException if the map is not properly initialized
     * @throws NumberFormatException if the coordinate parsing fails
     */
    default JLLatLng getCenter() {
        checkMapState();
        Object result = getJLEngine()
                .executeScript("this.map.getCenter();");
        String[] coords = result.toString().split(",");
        double lat = Double.parseDouble(coords[0].trim());
        double lng = Double.parseDouble(coords[1].trim());
        return new JLLatLng(lat, lng);
    }

    /**
     * Checks if the map is ready for operations.
     *
     * @throws JLMapNotReadyException if the map is not ready
     */
    default void checkMapState() {
        if (getJLEngine() == null) {
            throw new JLMapNotReadyException("Map engine is not initialized");
        }
    }

    private @Nullable <M extends LeafletLayer> M getLayerInternal(@NonNull Class<M> layerClass) {
        return getLayers().entrySet()
                .stream()
                .filter(entry -> layerClass.isAssignableFrom(entry.getKey()))
                .map(Map.Entry::getValue)
                .map(layerClass::cast)
                .findFirst()
                .orElse(null);
    }

    @Override
    default JLServerToClientTransporter<?> getTransport() {
        throw new UnsupportedOperationException("Use getJLEngine() instead");
    }

    @Override
    default JLMap<T> self() {
        return this;
    }

    @Override
    default JLMap<T> setJLObjectOpacity(double opacity) {
        getJLEngine()
                .executeScript(String.format("this.map.setOpacity(%f);", opacity));
        return this;
    }

    @Override
    default JLMap<T> setZIndexOffset(int offset) {
        getJLEngine()
                .executeScript(String.format("this.map.setZIndex(%d);", offset));
        return this;
    }

    @Override
    default String getJLId() {
        return "jl-map-view";
    }
}
