package io.github.makbn.jlmap.fx.demo;

import io.github.makbn.jlmap.JLMapController;
import io.github.makbn.jlmap.JLProperties;
import io.github.makbn.jlmap.fx.JLMapView;
import io.github.makbn.jlmap.listener.OnJLMapViewListener;
import io.github.makbn.jlmap.listener.OnJLObjectActionListener;
import io.github.makbn.jlmap.listener.event.ClickEvent;
import io.github.makbn.jlmap.listener.event.Event;
import io.github.makbn.jlmap.listener.event.MoveEvent;
import io.github.makbn.jlmap.listener.event.ZoomEvent;
import io.github.makbn.jlmap.map.JLMapProvider;
import io.github.makbn.jlmap.model.*;
import javafx.application.Application;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.paint.Color;
import javafx.stage.Screen;
import javafx.stage.Stage;
import lombok.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Matt Akbarian  (@makbn)
 */
public class LeafletTestJFX extends Application {
    private static final Logger log = LoggerFactory.getLogger(LeafletTestJFX.class);

    @Override
    public void start(Stage stage) {
        //building a new map view
        final JLMapView map = JLMapView
                .builder()
                .jlMapProvider(JLMapProvider.OSM_MAPNIK.build())
                .showZoomController(true)
                .startCoordinate(JLLatLng.builder()
                        .lat(51.044)
                        .lng(114.07)
                        .build())
                .build();
        //creating a window
        AnchorPane root = new AnchorPane(map);
        root.setBackground(Background.EMPTY);
        root.setMinHeight(JLProperties.INIT_MIN_HEIGHT_STAGE);
        root.setMinWidth(JLProperties.INIT_MIN_WIDTH_STAGE);
        Scene scene = new Scene(root);

        stage.setMinHeight(JLProperties.INIT_MIN_HEIGHT_STAGE);
        stage.setMinWidth(JLProperties.INIT_MIN_WIDTH_STAGE);
        scene.setFill(Color.TRANSPARENT);
        stage.setTitle("Java-Leaflet Test");
        stage.setScene(scene);
        stage.show();

        Rectangle2D primScreenBounds = Screen.getPrimary().getVisualBounds();
        stage.setX((primScreenBounds.getWidth() - stage.getWidth()) / 2);
        stage.setY(100);

        //set listener fo map events
        map.setMapListener(new OnJLMapViewListener() {
            @Override
            public void mapLoadedSuccessfully(@NonNull JLMapController mapView) {
                log.info("map loaded!");
                addMultiPolyline(map);
                addPolyline(map);
                addPolygon(map);

                map.setView(JLLatLng.builder()
                        .lng(10)
                        .lat(10)
                        .build());
                map.getUiLayer()
                        .addMarker(JLLatLng.builder()
                                .lat(35.63)
                                .lng(51.45)
                                .build(), "Tehran", true)
                        .setOnActionListener(getListener());

                map.getVectorLayer()
                        .addCircleMarker(JLLatLng.builder()
                                .lat(35.63)
                                .lng(40.45)
                                .build());

                map.getVectorLayer()
                        .addCircle(JLLatLng.builder()
                                .lat(35.63)
                                .lng(51.45)
                                .build(), 30000, JLOptions.DEFAULT);

                // JLImageOverlay demo: Eiffel Tower image over Paris
                JLBounds eiffelBounds = JLBounds.builder()
                        .southWest(JLLatLng.builder().lat(47.857).lng(3.293).build())
                        .northEast(JLLatLng.builder().lat(49.860).lng(1.298).build())
                        .build();
                map.getUiLayer().addImage(
                        eiffelBounds,
                        "https://img.favpng.com/1/24/8/eiffel-tower-eiffel-tower-illustrated-landmark-L5szYqrZ_t.jpg",
                        JLOptions.DEFAULT
                );

                // map zoom functionalities
                map.getControlLayer().setZoom(5);
                map.getControlLayer().zoomIn(2);
                map.getControlLayer().zoomOut(1);

                JLGeoJson geoJsonObject = map.getGeoJsonLayer()
                        .addFromUrl("https://pkgstore.datahub.io/examples/geojson-tutorial/example/data/db696b3bf628d9a273ca9907adcea5c9/example.geojson");

                log.info("geojson loaded! id: {}", geoJsonObject.getId());
            }

            @Override
            public void mapFailed() {
                log.error("map failed!");
            }

            @Override
            public void onActionReceived(Event event) {
                if (event instanceof MoveEvent moveEvent) {
                    log.info("move event: {} c: {} \t bounds: {} \t z: {}", moveEvent.action(), moveEvent.center(),
                            moveEvent.bounds(), moveEvent.zoomLevel());
                } else if (event instanceof ClickEvent clickEvent) {
                    log.info("click event: {}", clickEvent.center());
                    map.getUiLayer().addPopup(clickEvent.center(), "New Click Event!", JLOptions.builder()
                            .closeButton(false)
                            .autoClose(false).build());
                } else if (event instanceof ZoomEvent zoomEvent) {
                    log.info("zoom event: {}", zoomEvent.zoomLevel());
                }
            }
        });
    }

    private OnJLObjectActionListener<JLMarker> getListener() {
        return (jlMarker, event) -> log.info("object {} event for marker:{}", event.action(), jlMarker);
    }

    private void addMultiPolyline(JLMapView map) {
        JLLatLng[][] verticesT = new JLLatLng[2][];

        verticesT[0] = new JLLatLng[]{
                new JLLatLng(41.509, 20.08),
                new JLLatLng(31.503, -10.06),
                new JLLatLng(21.51, -0.047)
        };

        verticesT[1] = new JLLatLng[]{
                new JLLatLng(51.509, 10.08),
                new JLLatLng(55.503, 15.06),
                new JLLatLng(42.51, 20.047)
        };

        map.getVectorLayer().addMultiPolyline(verticesT);
    }

    private void addPolyline(JLMapView map) {
        JLLatLng[] vertices = new JLLatLng[]{
                new JLLatLng(51.509, -0.08),
                new JLLatLng(51.503, -0.06),
                new JLLatLng(51.51, -0.047)
        };

        map.getVectorLayer().addPolyline(vertices);
    }

    private void addPolygon(JLMapView map) {

        JLLatLng[][][] vertices = new JLLatLng[2][][];

        vertices[0] = new JLLatLng[2][];
        vertices[1] = new JLLatLng[1][];
        //first part
        vertices[0][0] = new JLLatLng[]{
                new JLLatLng(37, -109.05),
                new JLLatLng(41, -109.03),
                new JLLatLng(41, -102.05),
                new JLLatLng(37, -102.04)
        };
        //hole inside the first part
        vertices[0][1] = new JLLatLng[]{
                new JLLatLng(37.29, -108.58),
                new JLLatLng(40.71, -108.58),
                new JLLatLng(40.71, -102.50),
                new JLLatLng(37.29, -102.50)
        };
        //second part
        vertices[1][0] = new JLLatLng[]{
                new JLLatLng(41, -111.03),
                new JLLatLng(45, -111.04),
                new JLLatLng(45, -104.05),
                new JLLatLng(41, -104.05)
        };
        map.getVectorLayer().addPolygon(vertices).setOnActionListener(new OnJLObjectActionListener<>() {
            @Override
            public void onActionReceived(JLPolygon jlPolygon, Event event) {
                log.info("object {} event for jlPolygon:{}", event.action(), jlPolygon);
            }
        });
    }
}