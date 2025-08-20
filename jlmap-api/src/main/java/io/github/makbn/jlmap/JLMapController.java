package io.github.makbn.jlmap;

import io.github.makbn.jlmap.engine.JLWebEngine;
import io.github.makbn.jlmap.exception.JLMapNotReadyException;
import io.github.makbn.jlmap.layer.leaflet.*;
import io.github.makbn.jlmap.model.JLLatLng;
import lombok.NonNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Mehdi Akbarian Rastaghi (@makbn)
 */
public interface JLMapController {

    JLWebEngine getJLEngine();

    void addControllerToDocument();

    HashMap<Class<? extends LeafletLayer>, LeafletLayer> getLayers();

    /**
     * handle all functions for add/remove layers from UI layer
     *
     * @return current instance of {{@link LeafletLayer}}
     */
    default LeafletUILayerInt getUiLayer() {
        checkMapState();
        return getLayerInternal(LeafletUILayerInt.class);
    }

    /**
     * handle all functions for add/remove layers from Vector layer
     *
     * @return current instance of {{@link LeafletVectorLayerInt}}
     */
    default LeafletVectorLayerInt getVectorLayer() {
        checkMapState();
        return getLayerInternal(LeafletVectorLayerInt.class);
    }

    default LeafletControlLayerInt getControlLayer() {
        checkMapState();
        return getLayerInternal(LeafletControlLayerInt.class);
    }

    default LeafletGeoJsonLayerInt getGeoJsonLayer() {
        checkMapState();
        return getLayerInternal(LeafletGeoJsonLayerInt.class);
    }

    /**
     * Sets the view of the map (geographical center).
     *
     * @param latLng Represents a geographical point with a certain latitude
     *               and longitude.
     */
    default void setView(JLLatLng latLng) {
        checkMapState();
        getJLEngine()
                .executeScript(String.format("jlmap.panTo([%f, %f]);",
                        latLng.getLat(), latLng.getLng()));
    }

    /**
     * Sets the view of the map (geographical center) with animation duration.
     *
     * @param duration Represents the duration of transition animation.
     * @param latLng   Represents a geographical point with a certain latitude
     *                 and longitude.
     */
    default void setView(JLLatLng latLng, int duration) {
        checkMapState();
        getJLEngine()
                .executeScript(String.format("jlmap.panTo([%f, %f], %d);",
                        latLng.getLat(), latLng.getLng(), duration));
    }

    /**
     * Sets the zoom level of the map.
     *
     * @param zoomLevel Represents the zoom level of the map.
     */
    default void setZoom(int zoomLevel) {
        checkMapState();
        getJLEngine()
                .executeScript(String.format("jlmap.setZoom(%d);", zoomLevel));
    }

    /**
     * Gets the current zoom level of the map.
     *
     * @return current zoom level
     */
    default int getZoom() {
        checkMapState();
        Object result = getJLEngine()
                .executeScript("jlmap.getZoom();");
        return Integer.parseInt(result.toString());
    }

    /**
     * Gets the current center of the map.
     *
     * @return current center coordinates
     */
    default JLLatLng getCenter() {
        checkMapState();
        Object result = getJLEngine()
                .executeScript("jlmap.getCenter();");
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

    private @Nullable <T extends LeafletLayer> T getLayerInternal(@NonNull Class<T> layerClass) {
        return getLayers().entrySet()
                .stream()
                .filter(entry -> layerClass.isAssignableFrom(entry.getKey()))
                .map(Map.Entry::getValue)
                .map(layerClass::cast)
                .findFirst()
                .orElse(null);
    }
}
