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
import io.github.makbn.jlmap.model.JLLatLng;
import io.github.makbn.jlmap.model.JLMarker;
import io.github.makbn.jlmap.model.JLOptions;
import io.github.makbn.jlmap.vaadin.JLMapView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Matt Akbarian  (@makbn)
 */
@Route("")
public class HomeView extends HorizontalLayout implements OnJLMapViewListener {
    Logger log = LoggerFactory.getLogger(getClass());
    private JLMapView mapView;

    AtomicInteger defaultZoomLevel = new AtomicInteger(5);

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
                        .decimalField("Latitude")
                        .decimalField("Longitude")
                        .numberField("Zoom level")
                        .get(event -> mapView.getControlLayer().flyTo(
                                JLLatLng.builder()
                                        .lat((Double) event.get("Latitude"))
                                        .lng((Double) event.get("Longitude"))
                                        .build(), (Integer) event.get("Zoom level"))))
                .menu("UI Layer")
                .item("Add Marker", e ->
                        DialogBuilder.builder()
                                .decimalField("Latitude")
                                .decimalField("Longitude")
                                .textField("Text")
                                .get(event -> {
                                    JLMarker marker = mapView.getUiLayer().addMarker(JLLatLng.builder()
                                            .lat((Double) event.get("Latitude"))
                                            .lng((Double) event.get("Longitude"))
                                            .build(), (String) event.get("Text"), true);
                                    marker.setOnActionListener((jlMarker, event1) -> {
                                        if (event1 instanceof MoveEvent) {
                                            Notification.show("Marker moved: " + jlMarker + " -> " + event1.action());
                                        } else if (event1 instanceof ClickEvent) {
                                            Notification.show("Marker clicked: " + jlMarker);
                                        }
                                    });

                                    marker.getPopup().setOnActionListener((jlPopup, jlEvent) -> {
                                        Notification.show(String.format("Mareker's Popup '%s' Event: %s", jlPopup, jlEvent));
                                    });
                                }))
                .item("Hide Grid", e -> System.out.println("Grid off"))
                .menu("Geo Json Layer")
                .item("Load Data", e -> System.out.println("Loading GeoJSON..."))
                .menu("Vector Layer")
                .item("Draw Circle", e -> DialogBuilder.builder()
                        .decimalField("Latitude")
                        .decimalField("Longitude")
                        .numberField("Radius")
                        .get(event -> mapView.getVectorLayer().addCircle(
                                JLLatLng.builder()
                                        .lat((Double) event.get("Latitude"))
                                        .lng((Double) event.get("Longitude"))
                                        .build(), (Integer) event.get("Radius"),
                                JLOptions.DEFAULT.toBuilder().draggable(true).build()).setOnActionListener((jlCircle, jlEvent)
                                -> Notification.show(String.format("Circle '%s' Event: %s", jlCircle, jlEvent)))))
                .build();

        add(accordion);

        // Add components to the layout

        add(accordion);
        add(mapView);
        expand(mapView);
        addMarker();
    }


    /**
     * Creates the control panel with buttons for interacting with the map.
     *
     * @return the control panel
     */


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
    public void mapLoadedSuccessfully(io.github.makbn.jlmap.JLMapController mapController) {
        log.info("Map loaded successfully");
        Notification.show("Map loaded successfully");
    }

    @Override
    public void onActionReceived(Event event) {
        Notification.show("Map Action received: " + event);
    }
}
