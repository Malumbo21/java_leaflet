package io.github.makbn.jlmap.map;

import io.github.makbn.jlmap.JLProperties;
import io.github.makbn.jlmap.model.JLMapOption;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Singular;
import lombok.experimental.FieldDefaults;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Modern map tile provider configuration system for Java Leaflet.
 * <p>
 * This class replaces the legacy {@code JLProperties.MapType} enum system with a more flexible
 * and extensible approach to configuring map tile sources. It supports custom parameters,
 * attribution text, and various built-in providers.
 * </p>
 * <p>
 * The provider system supports both parameter-free providers (like OpenStreetMap) and
 * providers requiring API keys or custom configuration (like MapTiler).
 * </p>
 * <h3>Usage Examples:</h3>
 * <pre>{@code
 * // Built-in provider without parameters
 * JLMapProvider osmProvider = JLMapProvider.OSM_MAPNIK.build();
 *
 * // Provider with API key
 * JLMapProvider mapTilerProvider = JLMapProvider.MAP_TILER
 *     .parameter(new JLMapOption.Parameter("key", "your-api-key"))
 *     .build();
 *
 * // Custom provider
 * JLMapProvider customProvider = JLMapProvider.builder()
 *     .name("Custom Provider")
 *     .url("https://custom.tiles.example.com/{z}/{x}/{y}.png")
 *     .attribution("© Custom Maps")
 *     .maxZoom(18)
 *     .build();
 * }</pre>
 *
 * @author Matt Akbarian (@makbn)
 * @since 2.0.0
 */
@Builder(toBuilder = true)
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class JLMapProvider implements JLMapProviderInt {
    /** Built-in provider for OpenStreetMap standard tiles - no API key required */
    public static final JLMapProvider.JLMapProviderBuilder OSM_MAPNIK = new JLMapProvider("OpenStreetMap.Mapnik",
            "https://tile.openstreetmap.org/{z}/{x}/{y}.png",
            "&copy; <a href=\"https://www.openstreetmap.org/copyright\">OpenStreetMap</a> contributors",
            JLProperties.DEFAULT_MAX_ZOOM,
            new HashSet<>()).toBuilder();
    /** Built-in provider for MapTiler - requires API key parameter */
    public static final JLMapProvider.JLMapProviderBuilder MAP_TILER = new JLMapProvider("MapTiler",
            "https://api.maptiler.com/maps/aquarelle/256/{z}/{x}/{y}.png",
            "<a href=\"https://www.maptiler.com/copyright/\" target=\"_blank\">&copy; " +
                    "MapTiler</a> <a href=\"https://www.openstreetmap.org/copyright\" " +
                    "target=\"_blank\">&copy; OpenStreetMap contributors</a>",
            JLProperties.DEFAULT_MAX_ZOOM,
            new HashSet<>(),
            Set.of("key")).toBuilder();
    /**
     * Human-readable name of the map provider (e.g., "OpenStreetMap.Mapnik")
     */
    String name;
    /** Tile server URL template with {z}, {x}, {y} placeholders for zoom, x, and y coordinates */
    String url;
    /** Attribution text to display on the map (typically copyright and data source information) */
    String attribution;
    /** Maximum zoom level supported by this tile provider */
    int maxZoom;

    public JLMapProvider(String name, String url, String attribution, int maxZoom, Set<JLMapOption.Parameter> parameters, Set<String> requiredParameter) {
        this.name = name;
        this.url = url;
        this.attribution = attribution;
        this.maxZoom = maxZoom;
        this.parameters = parameters;
        this.requiredParameter = requiredParameter;
    }

    public JLMapProvider(String name, String url, String attribution, int maxZoom, Set<JLMapOption.Parameter> parameters) {
        this(name, url, attribution, maxZoom, parameters, Collections.emptySet());
    }
    /** Set of optional parameters (e.g., API keys) required by the tile provider */
    @Singular
    Set<JLMapOption.Parameter> parameters;

    public static final JLMapProvider.JLMapProviderBuilder OSM_GERMAN = new JLMapProvider("OpenStreetMap.German",
            "https://tile.openstreetmap.de/{z}/{x}/{y}.png",
            "&copy; <a href=\"https://www.openstreetmap.org/copyright\">OpenStreetMap</a> contributors",
            JLProperties.DEFAULT_MAX_ZOOM,
            new HashSet<>()).toBuilder();

    public static final JLMapProvider.JLMapProviderBuilder OSM_FRENCH = new JLMapProvider("OpenStreetMap.French",
            "https://{s}.tile.openstreetmap.fr/osmfr/{z}/{x}/{y}.png",
            "&copy; <a href=\"https://www.openstreetmap.org/copyright\">OpenStreetMap</a> contributors",
            JLProperties.DEFAULT_MAX_ZOOM,
            new HashSet<>()).toBuilder();

    public static final JLMapProvider.JLMapProviderBuilder OSM_HOT = new JLMapProvider("OpenStreetMap.HOT",
            "https://{s}.tile.openstreetmap.fr/hot/{z}/{x}/{y}.png",
            "&copy; <a href=\"https://www.openstreetmap.org/copyright\">OpenStreetMap</a> contributors, " +
                    "Tiles style by <a href=\"https://www.hotosm.org/\" target=\"_blank\">Humanitarian " +
                    "OpenStreetMap Team</a> hosted by <a href=\"https://openstreetmap.fr/\" target=\"_blank\">" +
                    "OpenStreetMap France</a>",
            JLProperties.DEFAULT_MAX_ZOOM,
            new HashSet<>()).toBuilder();

    public static final JLMapProvider.JLMapProviderBuilder OSM_CYCLE = new JLMapProvider("OpenStreetMap.CyclOSM",
            "https://{s}.tile.openstreetmap.fr/cyclosm/{z}/{x}/{y}.png",
            "&copy; <a href=\"https://www.openstreetmap.org/copyright\">OpenStreetMap</a> contributors, " +
                    "Tiles style by <a href=\"https://www.hotosm.org/\" target=\"_blank\">Humanitarian " +
                    "OpenStreetMap Team</a> hosted by <a href=\"https://openstreetmap.fr/\" target=\"_blank\">" +
                    "OpenStreetMap France</a>",
            JLProperties.DEFAULT_MAX_ZOOM,
            new HashSet<>()).toBuilder();

    public static final JLMapProvider.JLMapProviderBuilder OPEN_TOPO = new JLMapProvider("OpenTopoMap",
            "https://{s}.tile.opentopomap.org/{z}/{x}/{y}.png",
            "Map data: &copy; <a href=\"https://www.openstreetmap.org/copyright\">OpenStreetMap</a> " +
                    "contributors, <a href=\"http://viewfinderpanoramas.org\">SRTM</a> | Map style: &copy; " +
                    "<a href=\"https://opentopomap.org\">OpenTopoMap</a> " +
                    "(<a href=\"https://creativecommons.org/licenses/by-sa/3.0/\">CC-BY-SA</a>)",
            JLProperties.DEFAULT_MAX_ZOOM,
            new HashSet<>()).toBuilder();
    /** Set of required parameter names that must be provided for this provider to function */
    Set<String> requiredParameter;

    /**
     * Returns the default map provider (OpenStreetMap Mapnik).
     * <p>
     * This is a convenience method that provides a ready-to-use map provider
     * that works without any additional configuration or API keys.
     * </p>
     *
     * @return a configured {@link JLMapProvider} instance using OpenStreetMap tiles
     */
    public static JLMapProvider getDefault() {
        return OSM_MAPNIK.build();
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getUrl() {
        return url;
    }

    @Override
    public String getAttribution() {
        return attribution;
    }

    @Override
    public int getMaxZoom() {
        return maxZoom;
    }

    @Override
    public Set<String> getRequiredParametersName() {
        return requiredParameter;
    }

    @Override
    public Set<JLMapOption.Parameter> getParameters() {
        return parameters;
    }
}