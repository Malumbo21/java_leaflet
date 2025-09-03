package io.github.makbn.jlmap.fx.internal;

import io.github.makbn.jlmap.map.JLMapRenderer;
import io.github.makbn.jlmap.model.JLMapOption;
import lombok.NonNull;

import static j2html.TagCreator.*;

public class JLFxMapRenderer implements JLMapRenderer {

    private static final String LANG = "EN";
    private static final String TITLE = "JLMap Java - Leaflet";
    private static final String CSS_LEAFLET = "https://unpkg.com/leaflet@1.9.4/dist/leaflet.css";
    private static final String SCRIPT_LEAFLET = "https://unpkg.com/leaflet@1.9.4/dist/leaflet.js";
    private static final String SCRIPT_LEAFLET_PROVIDER = "https://cdn.jsdelivr.net/npm/leaflet-providers@2.0.0/leaflet-providers.min.js";


    @NonNull
    @Override
    public String render(@NonNull JLMapOption option) {
        return document().render() + html().withLang(LANG).with(
                head().with(
                        title(TITLE),
                        meta().withCharset("utf-8"),
                        meta().withName("viewport").withContent("width=device-width, initial-scale=1.0"),
                        link()
                                .withRel("stylesheet")
                                .withHref(CSS_LEAFLET)
                                .attr("integrity", "sha256-p4NxAoJBhIIN+hmNHrzRCf9tD/miZyoHS5obTRR9BMY=")
                                .attr("crossorigin", ""),
                        script()
                                .withSrc(SCRIPT_LEAFLET)
                                .attr("integrity", "sha256-20nQCchB9co0qIjJZRGuk2/Z9VM+kNiyxNV1lvTlZBo=")
                                .attr("crossorigin", ""),
                        script()
                                .withSrc(SCRIPT_LEAFLET_PROVIDER),
                        script(jsRelayFunction()),
                        script(mapHelperFunctions()),
                        script(clientToServerEventHandler())
                ),
                body().withStyle("margin: 0; background-color: #191a1a;").with(
                        div()
                                .withId("jl-map-view")
                                .withClass("leaflet-container leaflet-retina")
                                .withStyle("width: 100%; min-height: 100vh; height: 100vh; position: relative; background-color: #191a1a;"),
                        script(initializeMap(option))
                )
        ).render();
    }

    @NonNull
    private String mapHelperFunctions() {
        // language=js
        return """
                    function getCenterOfElement(element) {
                       if (element && element.getLatLng) {
                              return JSON.stringify({
                                lat: element.getLatLng().lat,
                                lng: element.getLatLng().lng
                              });
                            } else {
                              return JSON.stringify({
                                lat: this.map.getCenter().lat,
                                lng: this.map.getCenter().lng
                              });
                            }
                       }
                
                     function getMapBounds() {
                        return JSON.stringify({
                                        "northEast": {
                                            "lat": this.map.getBounds().getNorthEast().lat,
                                            "lng": this.map.getBounds().getNorthEast().lng,
                                        },
                                        "southWest": {
                                            "lat": this.map.getBounds().getSouthWest().lat,
                                            "lng": this.map.getBounds().getSouthWest().lng,
                                        }
                                    });
                        }
                """;
    }


    @NonNull
    private String jsRelayFunction() {
        //language=js
        return """
                function jlMapServerCallbackDelegate(functionName, param1, param2, param3, param4, param5) {
                    //do nothing
                }
                
                let fun = jlMapServerCallbackDelegate;
                jlMapServerCallbackDelegate = function () {
                    if ('serverCallback' in window) {
                        serverCallback.functionCalled(arguments[0], arguments[1], arguments[2], arguments[3], arguments[4], arguments[5]);                
                    }
                    fun.apply(this, arguments);
                }
                """;
    }

    @NonNull
    public String initializeMap(@NonNull JLMapOption option) {
        //language=js
        return """
                    this.jlMapElement = document.querySelector('#jl-map-view');
                    this.map = L.map(this.jlMapElement, {zoomControl: %b}).setView([%s, %s], %d);
                
                    L.tileLayer('%s').addTo(this.map);
                
                    this.outr_eventHandler = eventHandler;
                    this.jlMapElement.$server = {
                        eventHandler: (functionType, jlType, uuid, param1, param2, param3) => 
                            this.outr_eventHandler(functionType, jlType, uuid, param1, param2, param3)
                    };
                
                    this.map.jlid = 'main_map';
                
                    this.map.on('click', e => eventHandler('click', 'map', 'main_map', this.map.getZoom(), getCenterOfElement(), getMapBounds()));
                    this.map.on('move', e => eventHandler('move', 'map', 'main_map', this.map.getZoom(), getCenterOfElement(), getMapBounds()));
                    this.map.on('movestart', e => eventHandler('movestart', 'map', 'main_map', this.map.getZoom(), getCenterOfElement(), getMapBounds()));
                    this.map.on('moveend', e => eventHandler('moveend', 'map', 'main_map', this.map.getZoom(), getCenterOfElement(), getMapBounds()));
                    this.map.on('zoom', e => eventHandler('zoom', 'map', 'main_map', this.map.getZoom(), getCenterOfElement(), getMapBounds()));
                
                """.formatted(option.zoomControlEnabled(),
                option.getStartCoordinate().getLat(),
                option.getStartCoordinate().getLng(),
                option.getInitialZoom(),
                option.getJlMapProvider().getMapProviderAddress());
    }

    @NonNull
    private String clientToServerEventHandler() {
        //language=js
        return """
                function eventHandler(functionType, jlType, uuid, param1, param2, param3) {
                     jlMapServerCallbackDelegate(functionType, jlType, uuid, param1, param2, param3);
                }
                """;
    }

}
