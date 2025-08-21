package io.github.makbn.jlmap.map;

import io.github.makbn.jlmap.JLProperties;
import io.github.makbn.jlmap.model.JLMapOption;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public record MapType(String name, String url, String attribution, int maxZoom,
                      Set<JLMapOption.Parameter> parameters) implements JLMapProvider {


    public static final MapType OSM_MAPNIK = new MapType("OpenStreetMap.Mapnik",
            "https://tile.openstreetmap.org/{z}/{x}/{y}.png",
            "&copy; <a href=\"https://www.openstreetmap.org/copyright\">OpenStreetMap</a> contributors",
            JLProperties.DEFAULT_MAX_ZOOM,
            new HashSet<>());

    public static final MapType OSM_GERMAN = new MapType("OpenStreetMap.German",
            "https://tile.openstreetmap.de/{z}/{x}/{y}.png",
            "&copy; <a href=\"https://www.openstreetmap.org/copyright\">OpenStreetMap</a> contributors",
            JLProperties.DEFAULT_MAX_ZOOM,
            new HashSet<>());

    public static final MapType OSM_FRENCH = new MapType("OpenStreetMap.French",
            "https://{s}.tile.openstreetmap.fr/osmfr/{z}/{x}/{y}.png",
            "&copy; <a href=\"https://www.openstreetmap.org/copyright\">OpenStreetMap</a> contributors",
            JLProperties.DEFAULT_MAX_ZOOM,
            new HashSet<>());

    public static final MapType OSM_HOT = new MapType("OpenStreetMap.HOT",
            "https://{s}.tile.openstreetmap.fr/hot/{z}/{x}/{y}.png",
            "&copy; <a href=\"https://www.openstreetmap.org/copyright\">OpenStreetMap</a> contributors, " +
                    "Tiles style by <a href=\"https://www.hotosm.org/\" target=\"_blank\">Humanitarian " +
                    "OpenStreetMap Team</a> hosted by <a href=\"https://openstreetmap.fr/\" target=\"_blank\">" +
                    "OpenStreetMap France</a>",
            JLProperties.DEFAULT_MAX_ZOOM,
            new HashSet<>());

    public static final MapType OSM_CYCLE = new MapType("OpenStreetMap.CyclOSM",
            "https://{s}.tile.openstreetmap.fr/cyclosm/{z}/{x}/{y}.png",
            "&copy; <a href=\"https://www.openstreetmap.org/copyright\">OpenStreetMap</a> contributors, " +
                    "Tiles style by <a href=\"https://www.hotosm.org/\" target=\"_blank\">Humanitarian " +
                    "OpenStreetMap Team</a> hosted by <a href=\"https://openstreetmap.fr/\" target=\"_blank\">" +
                    "OpenStreetMap France</a>",
            JLProperties.DEFAULT_MAX_ZOOM,
            new HashSet<>());

    public static final MapType OPEN_TOPO = new MapType("OpenTopoMap",
            "https://{s}.tile.opentopomap.org/{z}/{x}/{y}.png",
            "Map data: &copy; <a href=\"https://www.openstreetmap.org/copyright\">OpenStreetMap</a> " +
                    "contributors, <a href=\"http://viewfinderpanoramas.org\">SRTM</a> | Map style: &copy; " +
                    "<a href=\"https://opentopomap.org\">OpenTopoMap</a> " +
                    "(<a href=\"https://creativecommons.org/licenses/by-sa/3.0/\">CC-BY-SA</a>)",
            JLProperties.DEFAULT_MAX_ZOOM,
            new HashSet<>());

    public static MapType getDefault() {
        return OSM_MAPNIK;
    }

    @Override
    public String getName() {
        return name();
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
    public Map<String, String> getParameters() {
        return parameters.stream()
                .collect(Collectors.toMap(JLMapOption.Parameter::key, JLMapOption.Parameter::value));
    }
}