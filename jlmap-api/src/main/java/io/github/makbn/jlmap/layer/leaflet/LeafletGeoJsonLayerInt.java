package io.github.makbn.jlmap.layer.leaflet;

import io.github.makbn.jlmap.exception.JLException;
import io.github.makbn.jlmap.model.JLGeoJson;
import io.github.makbn.jlmap.model.JLGeoJsonOptions;
import lombok.NonNull;

import java.io.File;

/**
 * The {@code LeafletGeoJsonLayerInt} interface defines methods for adding
 * and managing GeoJSON data layers in a Leaflet map.
 * <p>
 * Implementations of this interface should provide methods to add GeoJSON
 * data from various sources, such as files, URLs, or raw content, as well
 * as the ability to remove GeoJSON objects from the map.
 * <p>
 * Enhanced to support advanced GeoJSON features including custom styling,
 * filtering, and point-to-layer functions.
 *
 * @author Matt Akbarian (@makbn)
 */
public interface LeafletGeoJsonLayerInt extends LeafletLayer {

    /**
     * Adds a GeoJSON object from a file to the Leaflet map.
     *
     * @param file The {@link File} object representing the GeoJSON file to be added.
     * @return The {@link JLGeoJson} representing the added GeoJSON data.
     * @throws JLException If there is an error while adding the GeoJSON data.
     */
    JLGeoJson addFromFile(@NonNull File file) throws JLException;

    /**
     * Adds a GeoJSON object from a file to the Leaflet map with custom options.
     *
     * @param file    The {@link File} object representing the GeoJSON file to be added.
     * @param options Custom styling and configuration options for the GeoJSON layer.
     * @return The {@link JLGeoJson} representing the added GeoJSON data.
     * @throws JLException If there is an error while adding the GeoJSON data.
     */
    JLGeoJson addFromFile(@NonNull File file, @NonNull JLGeoJsonOptions options) throws JLException;

    /**
     * Adds a GeoJSON object from a URL to the Leaflet map.
     *
     * @param url The URL of the GeoJSON data to be added.
     * @return The {@link JLGeoJson} representing the added GeoJSON data.
     * @throws JLException If there is an error while adding the GeoJSON data.
     */
    JLGeoJson addFromUrl(@NonNull String url) throws JLException;

    /**
     * Adds a GeoJSON object from a URL to the Leaflet map with custom options.
     *
     * @param url     The URL of the GeoJSON data to be added.
     * @param options Custom styling and configuration options for the GeoJSON layer.
     * @return The {@link JLGeoJson} representing the added GeoJSON data.
     * @throws JLException If there is an error while adding the GeoJSON data.
     */
    JLGeoJson addFromUrl(@NonNull String url, @NonNull JLGeoJsonOptions options) throws JLException;

    /**
     * Adds a GeoJSON object from raw content to the Leaflet map.
     *
     * @param content The raw GeoJSON content to be added.
     * @return The {@link JLGeoJson} representing the added GeoJSON data.
     * @throws JLException If there is an error while adding the GeoJSON data.
     */
    JLGeoJson addFromContent(@NonNull String content) throws JLException;

    /**
     * Adds a GeoJSON object from raw content to the Leaflet map with custom options.
     *
     * @param content The raw GeoJSON content to be added.
     * @param options Custom styling and configuration options for the GeoJSON layer.
     * @return The {@link JLGeoJson} representing the added GeoJSON data.
     * @throws JLException If there is an error while adding the GeoJSON data.
     */
    JLGeoJson addFromContent(@NonNull String content, @NonNull JLGeoJsonOptions options) throws JLException;

    /**
     * Removes a GeoJSON object from the Leaflet map.
     *
     * @param id of the {@link JLGeoJson} to be removed from the map.
     * @return {@code true} if the removal was successful, {@code false}
     * if the object was not found or could not be removed.
     */
    boolean removeGeoJson(@NonNull String id);

}
