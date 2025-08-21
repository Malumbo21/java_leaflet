package io.github.makbn.vaadin.demo.views;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import io.github.makbn.jlmap.listener.OnJLMapViewListener;
import io.github.makbn.jlmap.listener.event.ClickEvent;
import io.github.makbn.jlmap.listener.event.Event;
import io.github.makbn.jlmap.listener.event.MoveEvent;
import io.github.makbn.jlmap.map.MapType;
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
public class HomeView extends FlexLayout implements OnJLMapViewListener {
    public static final String LATITUDE = "Latitude";
    public static final String LONGITUDE = "Longitude";
    private final transient Logger log = LoggerFactory.getLogger(getClass());
    private final JLMapView mapView;
    private final AtomicInteger defaultZoomLevel = new AtomicInteger(5);

    public HomeView() {
        setSizeFull();
        setFlexDirection(FlexDirection.ROW);
        getStyle().set("position", "relative");
        // Create the map view
        mapView = JLMapView.builder()
                .mapType(MapType.OSM_MAPNIK)
                .startCoordinate(new JLLatLng(48.864716, 2.349014)) // Paris
                .showZoomController(true)
                .build();
        mapView.setMapViewListener(this);
        mapView.setSizeFull();
        add(mapView);

        // --- PIXEL PERFECT JLMap MENU ---
        VerticalLayout menuWrapper = new VerticalLayout();
        menuWrapper.setClassName("jlmap-menu");
        menuWrapper.setPadding(false);
        menuWrapper.setSpacing(false);
        menuWrapper.setWidth(null);
        menuWrapper.setMinWidth("270px");
        menuWrapper.setMaxWidth("320px");
        menuWrapper.setHeightFull();
        menuWrapper.setAlignItems(Alignment.STRETCH);

        VerticalLayout menuContent = new VerticalLayout();
        menuContent.setClassName("jlmap-menu-content");
        menuContent.setPadding(false);
        menuContent.setSpacing(false);
        menuContent.setWidthFull();
        menuContent.setHeightFull();
        menuContent.getStyle().set("flex-grow", "1");
        menuContent.getStyle().set("overflow-y", "auto");

        final String MENU_ITEM_CLASS = "jlmap-menu-item";
        // Helper to create section
        java.util.function.BiFunction<String, Button[], VerticalLayout> section = (title, items) -> {
            VerticalLayout sec = new VerticalLayout();
            sec.setClassName("jlmap-menu-section");
            sec.setPadding(false);
            sec.setSpacing(false);
            Span label = new Span(title);
            label.setClassName("jlmap-menu-section-title");
            sec.add(label);
            sec.add(items);
            return sec;
        };

        // --- Control Layer ---
        Button zoomIn = new Button("Zoom in", e -> mapView.getControlLayer().setZoom(defaultZoomLevel.addAndGet(1)));
        Button zoomOut = new Button("Zoom out", e -> mapView.getControlLayer().setZoom(defaultZoomLevel.addAndGet(-1)));
        Button fitWorld = new Button("Fit World", e -> mapView.getControlLayer().fitWorld());
        Button maxZoom = new Button("Max Zoom", e -> DialogBuilder.builder().numberField("Max zoom level").get(event -> mapView.getControlLayer().setMaxZoom((Integer) event.get("Max zoom level"))));
        Button minZoom = new Button("Min Zoom", e -> DialogBuilder.builder().numberField("Min zoom level").get(event -> mapView.getControlLayer().setMinZoom((Integer) event.get("Min zoom level"))));
        Button flyTo = new Button("Fly to", e -> DialogBuilder.builder().decimalField(LATITUDE).decimalField(LONGITUDE).numberField("Zoom level").get(event -> mapView.getControlLayer().flyTo(JLLatLng.builder().lat((Double) event.get(LATITUDE)).lng((Double) event.get(LONGITUDE)).build(), (Integer) event.get("Zoom level"))));
        for (Button b : new Button[]{zoomIn, zoomOut, fitWorld, maxZoom, minZoom, flyTo})
            b.setClassName(MENU_ITEM_CLASS);
        menuContent.add(section.apply("Control Layer", new Button[]{zoomIn, zoomOut, fitWorld, maxZoom, minZoom, flyTo}));

        // --- UI Layer ---
        Button addMarker = new Button("Add Marker", e -> DialogBuilder.builder().decimalField(LATITUDE).decimalField(LONGITUDE).textField("Text").get(event -> {
            JLMarker marker = mapView.getUiLayer().addMarker(JLLatLng.builder().lat((Double) event.get(LATITUDE)).lng((Double) event.get(LONGITUDE)).build(), (String) event.get("Text"), true);
            marker.setOnActionListener((jlMarker, event1) -> {
                if (event1 instanceof MoveEvent) {
                    Notification.show("Marker moved: " + jlMarker + " -> " + event1.action());
                } else if (event1 instanceof ClickEvent) {
                    Notification.show("Marker clicked: " + jlMarker);
                }
            });
            marker.getPopup().setOnActionListener((jlPopup, jlEvent) -> Notification.show(String.format("Mareker's Popup '%s' Event: %s", jlPopup, jlEvent)));
        }));
        addMarker.setClassName(MENU_ITEM_CLASS);
        menuContent.add(section.apply("UI Layer", new Button[]{addMarker}));

        // --- Geo Json Layer ---
        Button loadGeoJson = new Button("Load Data", e -> log.info("Loading GeoJSON..."));
        loadGeoJson.setClassName(MENU_ITEM_CLASS);
        menuContent.add(section.apply("Geo Json Layer", new Button[]{loadGeoJson}));

        // --- Vector Layer ---
        Button drawCircle = new Button("Draw Circle", e -> DialogBuilder.builder().decimalField(LATITUDE).decimalField(LONGITUDE).numberField("Radius").get(event -> mapView.getVectorLayer().addCircle(JLLatLng.builder().lat((Double) event.get(LATITUDE)).lng((Double) event.get(LONGITUDE)).build(), (Integer) event.get("Radius"), JLOptions.DEFAULT.toBuilder().draggable(true).build()).setOnActionListener((jlCircle, jlEvent) -> Notification.show(String.format("Circle '%s' Event: %s", jlCircle, jlEvent)))));
        Button drawCircleMarker = new Button("Draw Circle Marker", e -> DialogBuilder.builder().decimalField(LATITUDE).decimalField(LONGITUDE).numberField("Radius (pixels)").get(event -> {
            JLCircleMarker circleMarker = mapView.getVectorLayer().addCircleMarker(JLLatLng.builder().lat((Double) event.get(LATITUDE)).lng((Double) event.get(LONGITUDE)).build(), (Integer) event.get("Radius (pixels)"), JLOptions.DEFAULT.toBuilder().color(JLColor.RED).build());
            circleMarker.setOnActionListener((jlCircleMarker, jlEvent) -> Notification.show(String.format("Circle Marker '%s' Event: %s", jlCircleMarker, jlEvent)));
        }));
        Button drawSimplePolyline = new Button("Draw Simple Polyline", e -> {
            JLLatLng[] vertices = {new JLLatLng(48.864716, 2.349014), new JLLatLng(52.520008, 13.404954), new JLLatLng(41.902783, 12.496366), new JLLatLng(40.416775, -3.703790)};
            JLPolyline polyline = mapView.getVectorLayer().addPolyline(vertices, JLOptions.DEFAULT.toBuilder().color(JLColor.BLUE).weight(5).build());
            polyline.setOnActionListener((jlPolyline, jlEvent) -> Notification.show(String.format("Polyline '%s' Event: %s", jlPolyline, jlEvent)));
            Notification.show("European Cities Route Added!");
        });
        Button drawCustomPolyline = new Button("Draw Custom Polyline", e -> DialogBuilder.builder().decimalField("Start Latitude").decimalField("Start Longitude").decimalField("Mid Latitude").decimalField("Mid Longitude").decimalField("End Latitude").decimalField("End Longitude").get(event -> {
            JLLatLng[] vertices = {new JLLatLng((Double) event.get("Start Latitude"), (Double) event.get("Start Longitude")), new JLLatLng((Double) event.get("Mid Latitude"), (Double) event.get("Mid Longitude")), new JLLatLng((Double) event.get("End Latitude"), (Double) event.get("End Longitude"))};
            JLPolyline polyline = mapView.getVectorLayer().addPolyline(vertices, JLOptions.DEFAULT.toBuilder().color(JLColor.GREEN).weight(3).build());
            polyline.setOnActionListener((jlPolyline, jlEvent) -> Notification.show(String.format("Custom Polyline '%s' Event: %s", jlPolyline, jlEvent)));
        }));
        Button drawMultiPolyline = new Button("Draw Multi-Polyline", e -> {
            JLLatLng[][] routes = {{new JLLatLng(59.334591, 18.063240), new JLLatLng(60.169857, 24.938379), new JLLatLng(55.676097, 12.568337)}, {new JLLatLng(50.075538, 14.437800), new JLLatLng(47.497912, 19.040235), new JLLatLng(48.208174, 16.373819)}};
            JLMultiPolyline multiPolyline = mapView.getVectorLayer().addMultiPolyline(routes, JLOptions.DEFAULT.toBuilder().color(JLColor.PURPLE).weight(4).build());
            multiPolyline.setOnActionListener((jlMultiPolyline, jlEvent) -> Notification.show(String.format("Multi-Polyline '%s' Event: %s", jlMultiPolyline, jlEvent)));
            Notification.show("Multi-Route Network Added!");
        });
        Button drawTrianglePolygon = new Button("Draw Triangle Polygon", e -> {
            JLLatLng[][][] triangleVertices = {{{new JLLatLng(48.864716, 2.349014), new JLLatLng(48.874716, 2.339014), new JLLatLng(48.854716, 2.339014), new JLLatLng(48.864716, 2.349014)}}};
            JLPolygon polygon = mapView.getVectorLayer().addPolygon(triangleVertices, JLOptions.DEFAULT.toBuilder().color(JLColor.ORANGE).fillColor(JLColor.YELLOW).fillOpacity(0.3).build());
            polygon.setOnActionListener((jlPolygon, jlEvent) -> Notification.show(String.format("Triangle Polygon '%s' Event: %s", jlPolygon, jlEvent)));
            Notification.show("Triangle Polygon Added around Paris!");
        });
        Button drawCustomPolygon = new Button("Draw Custom Polygon", e -> DialogBuilder.builder().decimalField("Center Latitude").decimalField("Center Longitude").decimalField("Size (degrees)").get(event -> {
            Double centerLat = (Double) event.get("Center Latitude");
            Double centerLng = (Double) event.get("Center Longitude");
            Double size = (Double) event.get("Size (degrees)");
            JLLatLng[][][] squareVertices = {{{new JLLatLng(centerLat + size, centerLng - size), new JLLatLng(centerLat + size, centerLng + size), new JLLatLng(centerLat - size, centerLng + size), new JLLatLng(centerLat - size, centerLng - size), new JLLatLng(centerLat + size, centerLng - size)}}};
            JLPolygon polygon = mapView.getVectorLayer().addPolygon(squareVertices, JLOptions.DEFAULT.toBuilder().color(JLColor.RED).fillColor(new JLColor(0.0, 1.0, 1.0)).fillOpacity(0.5).weight(3).build());
            polygon.setOnActionListener((jlPolygon, jlEvent) -> Notification.show(String.format("Custom Polygon '%s' Event: %s", jlPolygon, jlEvent)));
        }));
        Button drawPolygonWithHole = new Button("Draw Polygon with Hole", e -> {
            JLLatLng[][][] donutVertices = {{{new JLLatLng(48.874716, 2.329014), new JLLatLng(48.874716, 2.369014), new JLLatLng(48.854716, 2.369014), new JLLatLng(48.854716, 2.329014), new JLLatLng(48.874716, 2.329014)}, {new JLLatLng(48.869716, 2.339014), new JLLatLng(48.869716, 2.359014), new JLLatLng(48.859716, 2.359014), new JLLatLng(48.859716, 2.339014), new JLLatLng(48.869716, 2.339014)}}};
            JLPolygon donutPolygon = mapView.getVectorLayer().addPolygon(donutVertices, JLOptions.DEFAULT.toBuilder().color(new JLColor(0.0, 0.5, 0.0)).fillColor(new JLColor(0.5, 1.0, 0.5)).fillOpacity(0.7).weight(2).build());
            donutPolygon.setOnActionListener((jlPolygon, jlEvent) -> Notification.show(String.format("Donut Polygon '%s' Event: %s", jlPolygon, jlEvent)));
            Notification.show("Donut-shaped Polygon Added!");
        });
        Button demoAllShapes = new Button("Demo All Vector Shapes", e -> {
            mapView.getVectorLayer().addCircle(new JLLatLng(48.864716, 2.349014), 5000, JLOptions.DEFAULT.toBuilder().color(JLColor.BLUE).fillOpacity(0.2).build());
            mapView.getVectorLayer().addCircleMarker(new JLLatLng(48.874716, 2.359014), 10, JLOptions.DEFAULT.toBuilder().color(JLColor.RED).build());
            JLLatLng[] lineVertices = {new JLLatLng(48.854716, 2.339014), new JLLatLng(48.864716, 2.359014)};
            mapView.getVectorLayer().addPolyline(lineVertices, JLOptions.DEFAULT.toBuilder().color(JLColor.GREEN).weight(3).build());
            JLLatLng[][][] polygonVertices = {{{new JLLatLng(48.869716, 2.344014), new JLLatLng(48.869716, 2.354014), new JLLatLng(48.859716, 2.354014), new JLLatLng(48.859716, 2.344014), new JLLatLng(48.869716, 2.344014)}}};
            mapView.getVectorLayer().addPolygon(polygonVertices, JLOptions.DEFAULT.toBuilder().color(JLColor.PURPLE).fillColor(JLColor.YELLOW).fillOpacity(0.4).build());
            Notification.show("All vector shapes demonstrated! Check the map.");
        });
        for (Button b : new Button[]{drawCircle, drawCircleMarker, drawSimplePolyline, drawCustomPolyline, drawMultiPolyline, drawTrianglePolygon, drawCustomPolygon, drawPolygonWithHole, demoAllShapes})
            b.setClassName(MENU_ITEM_CLASS);
        menuContent.add(section.apply("Vector Layer", new Button[]{drawCircle, drawCircleMarker, drawSimplePolyline, drawCustomPolyline, drawMultiPolyline, drawTrianglePolygon, drawCustomPolygon, drawPolygonWithHole, demoAllShapes}));

        // --- GitHub Footer ---
        Anchor githubLink = new Anchor("https://github.com/makbn/java_leaflet", "");
        githubLink.setTarget("_blank");
        githubLink.setClassName("jlmap-menu-footer");
        githubLink.getElement().setProperty("innerHTML", "<span class='jlmap-github-icon' aria-hidden='true'>" +
                "<svg width='20' height='20' viewBox='0 0 16 16' fill='currentColor' xmlns='http://www.w3.org/2000/svg'><path d='M8 0C3.58 0 0 3.58 0 8c0 3.54 2.29 6.53 5.47 7.59.4.07.55-.17.55-.38 0-.19-.01-.82-.01-1.49-2.01.37-2.53-.49-2.69-.94-.09-.23-.48-.94-.82-1.13-.28-.15-.68-.52-.01-.53.63-.01 1.08.58 1.23.82.72 1.21 1.87.87 2.33.66.07-.52.28-.87.51-1.07-1.78-.2-3.64-.89-3.64-3.95 0-.87.31-1.59.82-2.15-.08-.2-.36-1.01.08-2.12 0 0 .67-.21 2.2.82.64-.18 1.32-.27 2-.27.68 0 1.36.09 2 .27 1.53-1.04 2.2-.82 2.2-.82.44 1.11.16 1.92.08 2.12.51.56.82 1.27.82 2.15 0 3.07-1.87 3.75-3.65 3.95.29.25.54.73.54 1.48 0 1.07-.01 1.93-.01 2.19 0 .21.15.46.55.38A8.013 8.013 0 0 0 16 8c0-4.42-3.58-8-8-8z'/></svg></span> <span>View on GitHub</span>");

        // Compose menu
        menuWrapper.add(menuContent, githubLink);
        add(menuWrapper);
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
