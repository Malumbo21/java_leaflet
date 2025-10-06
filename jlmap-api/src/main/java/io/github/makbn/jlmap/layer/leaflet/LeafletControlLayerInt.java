package io.github.makbn.jlmap.layer.leaflet;

import io.github.makbn.jlmap.model.JLBounds;
import io.github.makbn.jlmap.model.JLLatLng;

/**
 * Interface for programmatic control of map navigation, zooming, and viewport management.
 * <p>
 * This interface provides methods to manipulate the map's view state, including zoom levels,
 * geographic bounds, and animated transitions. All operations are smoothly animated unless
 * otherwise specified.
 * </p>
 * <p>
 * The control layer handles the following key operations:
 * </p>
 * <ul>
 *   <li><strong>Zoom Control</strong>: Increase, decrease, or set specific zoom levels</li>
 *   <li><strong>View Management</strong>: Pan to coordinates and set geographic bounds</li>
 *   <li><strong>Bounds Fitting</strong>: Automatically adjust view to fit geographic areas</li>
 *   <li><strong>Constraints</strong>: Set minimum and maximum zoom limits</li>
 *   <li><strong>Flight Animation</strong>: Smooth animated transitions between locations</li>
 * </ul>
 * <p>
 * <strong>Thread Safety:</strong> All methods should be called from the UI thread of the
 * respective framework (JavaFX Application Thread or Vaadin UI thread).
 * </p>
 *
 * @author Matt Akbarian  (@makbn)
 * @since 2.0.0
 */
public interface LeafletControlLayerInt extends LeafletLayer {

    /**
     * Smoothly increases the map zoom level by the specified amount.
     * <p>
     * The map will animate to the new zoom level while maintaining the current center point.
     * If the resulting zoom level exceeds the maximum allowed zoom, it will be clamped.
     * </p>
     *
     * @param delta the number of zoom levels to increase (must be positive)
     * @see <a href="https://leafletjs.com/reference.html#map-zoomin">leafletjs.com/reference.html#map-zoomin</a>
     */
    void zoomIn(int delta);

    /**
     * Smoothly decreases the map zoom level by the specified amount.
     * <p>
     * The map will animate to the new zoom level while maintaining the current center point.
     * If the resulting zoom level is below the minimum allowed zoom, it will be clamped.
     * </p>
     *
     * @param delta the number of zoom levels to decrease (must be positive)
     * @see <a href="https://leafletjs.com/reference.html#map-zoomout">
     * leafletjs.com/reference.html#map-zoomout</a>
     */
    void zoomOut(int delta);

    /**
     * Animates the map to the specified zoom level.
     * <p>
     * The map will smoothly transition to the new zoom level while keeping the current
     * center point fixed. Values outside the allowed zoom range will be automatically
     * clamped to valid bounds.
     * </p>
     *
     * @param level the target zoom level (typically 0-19, depending on tile provider)
     * @see <a href="https://leafletjs.com/reference.html#map-setzoom">
     * leafletjs.com/reference.html#map-setzoom</a>
     */
    void setZoom(int level);

    /**
     * Zooms the map while keeping a specified geographical point on the map
     * stationary (e.g. used internally for scroll zoom and double-click zoom)
     *
     * @see <a href="https://leafletjs.com/reference.html#map-setzoomaround">
     * leafletjs.com/reference.html#map-setzoomaround</a>
     */
    void setZoomAround(JLLatLng latLng, int zoom);


    /**
     * Sets a map view that contains the given geographical bounds with the
     * maximum zoom level possible.
     *
     * @param bounds The geographical bounds to fit.
     * @see <a href="https://leafletjs.com/reference.html#map-fitbounds">
     * leafletjs.com/reference.html#map-fitbounds</a>
     */
    void fitBounds(JLBounds bounds);

    /**
     * Sets a map view that mostly contains the whole world with the maximum
     * zoom level possible.
     *
     * @see <a href="https://leafletjs.com/reference.html#map-fitworld">
     * leafletjs.com/reference.html#map-fitworld</a>
     */
    void fitWorld();

    /**
     * Pans the map to a given latLng.
     *
     * @param latLng The new latLng of the map.
     * @see <a href="https://leafletjs.com/reference.html#map-panto">
     * leafletjs.com/reference.html#map-panto</a>
     */
    void panTo(JLLatLng latLng);

    /**
     * Sets the view of the map (geographical latLng and zoom) performing a
     * smooth pan-zoom animation.
     *
     * @param latLng The new latLng of the map.
     * @param zoom   The new zoom level (optional).
     * @see <a href="https://leafletjs.com/reference.html#map-flyto">
     * leafletjs.com/reference.html#map-flyto</a>
     */
    void flyTo(JLLatLng latLng, int zoom);

    /**
     * Sets the view of the map with a smooth animation like flyTo, but
     * takes a bounds parameter like fitBounds.
     *
     * @param bounds The bounds to fit the map view to.
     * @see <a href="https://leafletjs.com/reference.html#map-flytobounds">
     * leafletjs.com/reference.html#map-flytobounds</a>
     */
    void flyToBounds(JLBounds bounds);

    /**
     * Restricts the map view to the given bounds.
     *
     * @param bounds The geographical bounds to restrict the map view to.
     * @see <a href="https://leafletjs.com/reference.html#map-setmaxbounds">
     * leafletjs.com/reference.html#map-setmaxbounds</a>
     */
    void setMaxBounds(JLBounds bounds);

    /**
     * Sets the lower limit for the available zoom levels.
     *
     * @param zoom The minimum zoom level.
     * @see <a href="https://leafletjs.com/reference.html#map-setminzoom">
     * leafletjs.com/reference.html#map-setminzoom</a>
     */
    void setMinZoom(int zoom);

    /**
     * Sets the upper limit for the available zoom levels.
     *
     * @param zoom The maximum zoom level.
     * @see <a href="https://leafletjs.com/reference.html#map-setmaxzoom">
     * leafletjs.com/reference.html#map-setmaxzoom</a>
     */
    void setMaxZoom(int zoom);

    /**
     * Pans the map to the closest view that would lie inside the given bounds.
     *
     * @param bounds The bounds to pan inside.
     * @see <a href="https://leafletjs.com/reference.html#map-paninsidebounds">
     * leafletjs.com/reference.html#map-paninsidebounds</a>
     */
    void panInsideBounds(JLBounds bounds);

    /**
     * Pans the map the minimum amount to make the latLng visible.
     *
     * @param latLng The geographical point to make visible.
     * @see <a href="https://leafletjs.com/reference.html#map-paninside">
     * leafletjs.com/reference.html#map-paninside</a>
     */
    void panInside(JLLatLng latLng);

}
