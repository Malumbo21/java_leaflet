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

@Builder(toBuilder = true)
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class JLMapProvider implements JLMapProviderInt {
    String name;
    String url;
    String attribution;
    int maxZoom;
    @Singular
    Set<JLMapOption.Parameter> parameters;
    Set<String> requiredParameter;

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

    public static final JLMapProvider.JLMapProviderBuilder OSM_MAPNIK = new JLMapProvider("OpenStreetMap.Mapnik",
            "https://tile.openstreetmap.org/{z}/{x}/{y}.png",
            "&copy; <a href=\"https://www.openstreetmap.org/copyright\">OpenStreetMap</a> contributors",
            JLProperties.DEFAULT_MAX_ZOOM,
            new HashSet<>()).toBuilder();

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

    public static final JLMapProvider.JLMapProviderBuilder MAP_TILER = new JLMapProvider("MapTiler",
            "https://api.maptiler.com/maps/aquarelle/256/{z}/{x}/{y}.png",
            "<a href=\"https://www.maptiler.com/copyright/\" target=\"_blank\">&copy; " +
                    "MapTiler</a> <a href=\"https://www.openstreetmap.org/copyright\" " +
                    "target=\"_blank\">&copy; OpenStreetMap contributors</a>",
            JLProperties.DEFAULT_MAX_ZOOM,
            new HashSet<>(),
            Set.of("key")).toBuilder();

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