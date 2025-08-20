package io.github.makbn.vaadin.demo.views;

import com.vaadin.flow.component.accordion.Accordion;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.router.Route;
import io.github.makbn.jlmap.JLProperties;
import io.github.makbn.jlmap.listener.OnJLMapViewListener;
import io.github.makbn.jlmap.listener.event.ClickEvent;
import io.github.makbn.jlmap.listener.event.Event;
import io.github.makbn.jlmap.listener.event.MoveEvent;
import io.github.makbn.jlmap.model.*;
import io.github.makbn.jlmap.vaadin.JLMapView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.util.annotation.NonNull;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Matt Akbarian  (@makbn)
 */
@Route("")
public class HomeView extends HorizontalLayout implements OnJLMapViewListener {
    public static final String LATITUDE = "Latitude";
    public static final String LONGITUDE = "Longitude";
    private final transient Logger log = LoggerFactory.getLogger(getClass());
    private final JLMapView mapView;
    private final AtomicInteger defaultZoomLevel = new AtomicInteger(5);

    public HomeView() {
        setSizeFull();
        // Create the map view
        mapView = JLMapView.builder()
                .mapType(JLProperties.MapType.OSM_MAPNIK)
                .startCoordinate(new JLLatLng(48.864716, 2.349014)) // Paris
                .showZoomController(true)
                .build();
        mapView.setMapViewListener(this);

        Accordion accordion = new Accordion();

        new AccordionMenuBuilder(accordion)
                .menu("Control Layer")
                .item("Zoom in", e -> mapView.getControlLayer().setZoom(defaultZoomLevel.addAndGet(1)))
                .item("Zoom out", e -> mapView.getControlLayer().setZoom(defaultZoomLevel.addAndGet(-1)))
                .item("Fit World", e -> mapView.getControlLayer().fitWorld())
                .item("Max Zoom", e -> DialogBuilder.builder()
                        .numberField("Max zoom level")
                        .get(event -> mapView.getControlLayer().setMaxZoom((Integer) event.get("Max zoom level"))))
                .item("Min Zoom", e -> DialogBuilder.builder()
                        .numberField("Min zoom level")
                        .get(event -> mapView.getControlLayer().setMinZoom((Integer) event.get("Min zoom level"))))
                .item("Fly to", e -> DialogBuilder.builder()
                        .decimalField(LATITUDE)
                        .decimalField(LONGITUDE)
                        .numberField("Zoom level")
                        .get(event -> mapView.getControlLayer().flyTo(
                                JLLatLng.builder()
                                        .lat((Double) event.get(LATITUDE))
                                        .lng((Double) event.get(LONGITUDE))
                                        .build(), (Integer) event.get("Zoom level"))))
                .menu("UI Layer")
                .item("Add Marker", e ->
                        DialogBuilder.builder()
                                .decimalField(LATITUDE)
                                .decimalField(LONGITUDE)
                                .textField("Text")
                                .get(event -> {
                                    JLMarker marker = mapView.getUiLayer().addMarker(JLLatLng.builder()
                                            .lat((Double) event.get(LATITUDE))
                                            .lng((Double) event.get(LONGITUDE))
                                            .build(), (String) event.get("Text"), true);
                                    marker.setOnActionListener((jlMarker, event1) -> {
                                        if (event1 instanceof MoveEvent) {
                                            Notification.show("Marker moved: " + jlMarker + " -> " + event1.action());
                                        } else if (event1 instanceof ClickEvent) {
                                            Notification.show("Marker clicked: " + jlMarker);
                                        }
                                    });

                                    marker.getPopup().setOnActionListener((jlPopup, jlEvent) ->
                                            Notification.show(String.format("Mareker's Popup '%s' Event: %s", jlPopup, jlEvent))
                                    );
                                }))
                .menu("Geo Json Layer")
                .item("Load Data", e -> log.info("Loading GeoJSON..."))
                .menu("Vector Layer")
                .item("Draw Circle", e -> DialogBuilder.builder()
                        .decimalField(LATITUDE)
                        .decimalField(LONGITUDE)
                        .numberField("Radius")
                        .get(event -> mapView.getVectorLayer().addCircle(
                                JLLatLng.builder()
                                        .lat((Double) event.get(LATITUDE))
                                        .lng((Double) event.get(LONGITUDE))
                                        .build(), (Integer) event.get("Radius"),
                                JLOptions.DEFAULT.toBuilder().draggable(true).build()).setOnActionListener((jlCircle, jlEvent)
                                -> Notification.show(String.format("Circle '%s' Event: %s", jlCircle, jlEvent)))))
                
                // NEW: Circle Marker Demo
                .item("Draw Circle Marker", e -> DialogBuilder.builder()
                        .decimalField(LATITUDE)
                        .decimalField(LONGITUDE)
                        .numberField("Radius (pixels)")
                        .get(event -> {
                            JLCircleMarker circleMarker = mapView.getVectorLayer().addCircleMarker(
                                    JLLatLng.builder()
                                            .lat((Double) event.get(LATITUDE))
                                            .lng((Double) event.get(LONGITUDE))
                                            .build(), 
                                    (Integer) event.get("Radius (pixels)"),
                                    JLOptions.DEFAULT.toBuilder().color(JLColor.RED).build());
                            circleMarker.setOnActionListener((jlCircleMarker, jlEvent) ->
                                    Notification.show(String.format("Circle Marker '%s' Event: %s", jlCircleMarker, jlEvent)));
                        }))
                
                // NEW: Simple Polyline Demo
                .item("Draw Simple Polyline", e -> {
                    // Create a simple polyline connecting major European cities
                    JLLatLng[] vertices = {
                            new JLLatLng(48.864716, 2.349014),  // Paris
                            new JLLatLng(52.520008, 13.404954), // Berlin
                            new JLLatLng(41.902783, 12.496366), // Rome
                            new JLLatLng(40.416775, -3.703790)  // Madrid
                    };
                    JLPolyline polyline = mapView.getVectorLayer().addPolyline(vertices, 
                            JLOptions.DEFAULT.toBuilder().color(JLColor.BLUE).weight(5).build());
                    polyline.setOnActionListener((jlPolyline, jlEvent) ->
                            Notification.show(String.format("Polyline '%s' Event: %s", jlPolyline, jlEvent)));
                    Notification.show("European Cities Route Added!");
                })
                
                // NEW: Custom Polyline Demo
                .item("Draw Custom Polyline", e -> DialogBuilder.builder()
                        .decimalField("Start Latitude")
                        .decimalField("Start Longitude")
                        .decimalField("Mid Latitude")
                        .decimalField("Mid Longitude")
                        .decimalField("End Latitude")
                        .decimalField("End Longitude")
                        .get(event -> {
                            JLLatLng[] vertices = {
                                    new JLLatLng((Double) event.get("Start Latitude"), (Double) event.get("Start Longitude")),
                                    new JLLatLng((Double) event.get("Mid Latitude"), (Double) event.get("Mid Longitude")),
                                    new JLLatLng((Double) event.get("End Latitude"), (Double) event.get("End Longitude"))
                            };
                            JLPolyline polyline = mapView.getVectorLayer().addPolyline(vertices,
                                    JLOptions.DEFAULT.toBuilder().color(JLColor.GREEN).weight(3).build());
                            polyline.setOnActionListener((jlPolyline, jlEvent) ->
                                    Notification.show(String.format("Custom Polyline '%s' Event: %s", jlPolyline, jlEvent)));
                        }))
                
                // NEW: Multi-Polyline Demo
                .item("Draw Multi-Polyline", e -> {
                    // Create multiple connected routes
                    JLLatLng[][] routes = {
                            { // Route 1: Northern Europe
                                    new JLLatLng(59.334591, 18.063240), // Stockholm
                                    new JLLatLng(60.169857, 24.938379), // Helsinki
                                    new JLLatLng(55.676097, 12.568337)  // Copenhagen
                            },
                            { // Route 2: Central Europe
                                    new JLLatLng(50.075538, 14.437800), // Prague
                                    new JLLatLng(47.497912, 19.040235), // Budapest
                                    new JLLatLng(48.208174, 16.373819)  // Vienna
                            }
                    };
                    JLMultiPolyline multiPolyline = mapView.getVectorLayer().addMultiPolyline(routes,
                            JLOptions.DEFAULT.toBuilder().color(JLColor.PURPLE).weight(4).build());
                    multiPolyline.setOnActionListener((jlMultiPolyline, jlEvent) ->
                            Notification.show(String.format("Multi-Polyline '%s' Event: %s", jlMultiPolyline, jlEvent)));
                    Notification.show("Multi-Route Network Added!");
                })
                
                // NEW: Simple Polygon Demo
                .item("Draw Triangle Polygon", e -> {
                    // Create a triangle polygon around Paris
                    JLLatLng[][][] triangleVertices = {{
                            {
                                    new JLLatLng(48.864716, 2.349014),   // Paris center
                                    new JLLatLng(48.874716, 2.339014),   // Northwest
                                    new JLLatLng(48.854716, 2.339014),   // Southwest
                                    new JLLatLng(48.864716, 2.349014)    // Close the triangle
                            }
                    }};
                    JLPolygon polygon = mapView.getVectorLayer().addPolygon(triangleVertices,
                            JLOptions.DEFAULT.toBuilder()
                                    .color(JLColor.ORANGE)
                                    .fillColor(JLColor.YELLOW)
                                    .fillOpacity(0.3)
                                    .build());
                    polygon.setOnActionListener((jlPolygon, jlEvent) ->
                            Notification.show(String.format("Triangle Polygon '%s' Event: %s", jlPolygon, jlEvent)));
                    Notification.show("Triangle Polygon Added around Paris!");
                })
                
                // NEW: Complex Polygon Demo
                .item("Draw Custom Polygon", e -> DialogBuilder.builder()
                        .decimalField("Center Latitude")
                        .decimalField("Center Longitude")
                        .decimalField("Size (degrees)")
                        .get(event -> {
                            Double centerLat = (Double) event.get("Center Latitude");
                            Double centerLng = (Double) event.get("Center Longitude");
                            Double size = (Double) event.get("Size (degrees)");
                            
                            // Create a square polygon
                            JLLatLng[][][] squareVertices = {{
                                    {
                                            new JLLatLng(centerLat + size, centerLng - size), // Top-left
                                            new JLLatLng(centerLat + size, centerLng + size), // Top-right
                                            new JLLatLng(centerLat - size, centerLng + size), // Bottom-right
                                            new JLLatLng(centerLat - size, centerLng - size), // Bottom-left
                                            new JLLatLng(centerLat + size, centerLng - size)  // Close the square
                                    }
                            }};
                            JLPolygon polygon = mapView.getVectorLayer().addPolygon(squareVertices,
                                    JLOptions.DEFAULT.toBuilder()
                                            .color(JLColor.RED)
                                            .fillColor(new JLColor(0.0, 1.0, 1.0)) // CYAN equivalent
                                            .fillOpacity(0.5)
                                            .weight(3)
                                            .build());
                            polygon.setOnActionListener((jlPolygon, jlEvent) ->
                                    Notification.show(String.format("Custom Polygon '%s' Event: %s", jlPolygon, jlEvent)));
                        }))
                
                // NEW: Polygon with Hole Demo
                .item("Draw Polygon with Hole", e -> {
                    // Create a polygon with a hole (like a donut)
                    JLLatLng[][][] donutVertices = {{
                            { // Outer ring
                                    new JLLatLng(48.874716, 2.329014),
                                    new JLLatLng(48.874716, 2.369014),
                                    new JLLatLng(48.854716, 2.369014),
                                    new JLLatLng(48.854716, 2.329014),
                                    new JLLatLng(48.874716, 2.329014)
                            },
                            { // Inner ring (hole)
                                    new JLLatLng(48.869716, 2.339014),
                                    new JLLatLng(48.869716, 2.359014),
                                    new JLLatLng(48.859716, 2.359014),
                                    new JLLatLng(48.859716, 2.339014),
                                    new JLLatLng(48.869716, 2.339014)
                            }
                    }};
                    JLPolygon donutPolygon = mapView.getVectorLayer().addPolygon(donutVertices,
                            JLOptions.DEFAULT.toBuilder()
                                    .color(new JLColor(0.0, 0.5, 0.0)) // DARK_GREEN equivalent
                                    .fillColor(new JLColor(0.5, 1.0, 0.5)) // LIGHT_GREEN equivalent
                                    .fillOpacity(0.7)
                                    .weight(2)
                                    .build());
                    donutPolygon.setOnActionListener((jlPolygon, jlEvent) ->
                            Notification.show(String.format("Donut Polygon '%s' Event: %s", jlPolygon, jlEvent)));
                    Notification.show("Donut-shaped Polygon Added!");
                })
                
                // NEW: Demo All Shapes at Once
                .item("Demo All Vector Shapes", e -> {
                    // Add one of each shape type for demonstration
                    
                    // Circle
                    mapView.getVectorLayer().addCircle(
                            new JLLatLng(48.864716, 2.349014), 5000,
                            JLOptions.DEFAULT.toBuilder().color(JLColor.BLUE).fillOpacity(0.2).build());
                    
                    // Circle Marker
                    mapView.getVectorLayer().addCircleMarker(
                            new JLLatLng(48.874716, 2.359014), 10,
                            JLOptions.DEFAULT.toBuilder().color(JLColor.RED).build());
                    
                    // Polyline
                    JLLatLng[] lineVertices = {
                            new JLLatLng(48.854716, 2.339014),
                            new JLLatLng(48.864716, 2.359014)
                    };
                    mapView.getVectorLayer().addPolyline(lineVertices,
                            JLOptions.DEFAULT.toBuilder().color(JLColor.GREEN).weight(3).build());
                    
                    // Polygon
                    JLLatLng[][][] polygonVertices = {{
                            {
                                    new JLLatLng(48.869716, 2.344014),
                                    new JLLatLng(48.869716, 2.354014),
                                    new JLLatLng(48.859716, 2.354014),
                                    new JLLatLng(48.859716, 2.344014),
                                    new JLLatLng(48.869716, 2.344014)
                            }
                    }};
                    mapView.getVectorLayer().addPolygon(polygonVertices,
                            JLOptions.DEFAULT.toBuilder()
                                    .color(JLColor.PURPLE)
                                    .fillColor(JLColor.YELLOW)
                                    .fillOpacity(0.4)
                                    .build());
                    
                    Notification.show("All vector shapes demonstrated! Check the map.");
                })
                .build();

        add(accordion);

        // Add components to the layout

        add(accordion);
        add(mapView);
        expand(mapView);
        addMarker();
    }

    /**
     * Adds a marker to the map at the current center.
     */
    private void addMarker() {
        JLLatLng center = JLLatLng.builder()
                .lat(51.0207537)
                .lng(-114.1032121)
                .build();

        JLMarker marker = JLMarker.builder()
                .latLng(center)
                .text("Marker at " + center.getLat() + ", " + center.getLng())
                .build();

        marker.setOnActionListener((jlMarker, event) ->
                log.info("Marker event {} at: {}", event.action(), jlMarker.getLatLng()));

        //mapView.getUiLayer().addMarker()
        Notification.show("Marker added at " + center.getLat() + ", " + center.getLng());
    }

    /**
     * Called when the map is loaded successfully.
     *
     * @param mapController the map controller
     */
    @Override
    public void mapLoadedSuccessfully(@NonNull io.github.makbn.jlmap.JLMapController mapController) {
        log.info("Map loaded successfully");
        Notification.show("Map loaded successfully");
    }

    @Override
    public void onActionReceived(Event event) {
        Notification.show("Map Action received: " + event);
    }
}
