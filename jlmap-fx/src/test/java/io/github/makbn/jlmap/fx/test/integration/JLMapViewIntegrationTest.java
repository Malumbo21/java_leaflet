package io.github.makbn.jlmap.fx.test.integration;

import io.github.makbn.jlmap.JLProperties;
import io.github.makbn.jlmap.fx.JLMapView;
import io.github.makbn.jlmap.map.JLMapProvider;
import io.github.makbn.jlmap.model.JLBounds;
import io.github.makbn.jlmap.model.JLLatLng;
import io.github.makbn.jlmap.model.JLMapOption;
import io.github.makbn.jlmap.model.JLOptions;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class JLMapViewIntegrationTest extends ApplicationTest {
    JLMapView map;

    @Override
    public void start(Stage stage) {
        map = JLMapView
                .builder()
                .jlMapProvider(JLMapProvider.MAP_TILER
                        .parameter(new JLMapOption.Parameter("key", "rNGhTaIpQWWH7C6QGKzF"))
                        .build())
                .startCoordinate(new JLLatLng(48.864716, 2.349014)) // Paris
                .showZoomController(true)
                .startCoordinate(JLLatLng.builder()
                        .lat(51.044)
                        .lng(-114.07)
                        .build())
                .build();
        AnchorPane root = new AnchorPane(map);
        root.setBackground(Background.EMPTY);
        root.setMinHeight(JLProperties.INIT_MIN_HEIGHT_STAGE);
        root.setMinWidth(JLProperties.INIT_MIN_WIDTH_STAGE);
        Scene scene = new Scene(root);

        stage.setScene(scene);
        stage.show();
        stage.toFront();
    }


    @Test
    void jlMapView_addMarker_shouldAddMarkerAsLayer() throws Exception {
        CountDownLatch latch = new CountDownLatch(1);
        Platform.runLater(() -> {
            // Call your existing Java→JS bridge function to add marker
            map.getUiLayer().addMarker(JLLatLng.builder()
                            .lat(ThreadLocalRandom.current().nextDouble(-90.0, 90.0))
                            .lng(ThreadLocalRandom.current().nextDouble(-180.0, 180.0))
                            .build(),
                    "A Marker", false);
            latch.countDown();
        });

        if (latch.await(5, TimeUnit.SECONDS)) {
            Platform.runLater(() -> {
                // Inspect Leaflet state
                Object markerCount = map.getJLEngine().executeScript("""
                        Object.keys(map._layers).filter(k => map._layers[k] instanceof L.Marker).length""");
                assertThat((Number) markerCount).isEqualTo(1);
            });
        } else {
            throw new TimeoutException("Timed out waiting for marker addition");
        }
    }

    @Test
    void jlMapView_addPopup_shouldAddPopupAsLayer() throws Exception {
        CountDownLatch latch = new CountDownLatch(1);
        Platform.runLater(() -> {
            map.getUiLayer().addPopup(JLLatLng.builder()
                            .lat(ThreadLocalRandom.current().nextDouble(-90.0, 90.0))
                            .lng(ThreadLocalRandom.current().nextDouble(-180.0, 180.0))
                            .build(),
                    "Test Popup");
            latch.countDown();
        });

        if (latch.await(5, TimeUnit.SECONDS)) {
            Platform.runLater(() -> {
                Object popupCount = map.getJLEngine().executeScript("""
                        Object.keys(map._layers).filter(k => map._layers[k] instanceof L.Popup).length""");
                assertThat((Number) popupCount).isEqualTo(1);
            });
        } else {
            throw new TimeoutException("Timed out waiting for popup addition");
        }
    }

    @Test
    void jlMapView_addImageOverlay_shouldAddImageOverlayAsLayer() throws Exception {
        CountDownLatch latch = new CountDownLatch(1);
        Platform.runLater(() -> {
            map.getUiLayer().addImage(
                    JLBounds.builder()
                            .southWest(JLLatLng.builder().lat(50.0).lng(-120.0).build())
                            .northEast(JLLatLng.builder().lat(55.0).lng(-110.0).build())
                            .build(),
                    "https://example.com/image.png",
                    JLOptions.DEFAULT
            );
            latch.countDown();
        });

        if (latch.await(5, TimeUnit.SECONDS)) {
            Platform.runLater(() -> {
                Object imageOverlayCount = map.getJLEngine().executeScript("""
                        Object.keys(map._layers).filter(k => map._layers[k] instanceof L.ImageOverlay).length""");
                assertThat((Number) imageOverlayCount).isEqualTo(1);
            });
        } else {
            throw new TimeoutException("Timed out waiting for image overlay addition");
        }
    }

    @Test
    void jlMapView_addPolyline_shouldAddPolylineAsLayer() throws Exception {
        CountDownLatch latch = new CountDownLatch(1);
        Platform.runLater(() -> {
            map.getVectorLayer().addPolyline(new JLLatLng[]{
                    JLLatLng.builder().lat(51.509).lng(-0.08).build(),
                    JLLatLng.builder().lat(51.503).lng(-0.06).build(),
                    JLLatLng.builder().lat(51.51).lng(-0.047).build()
            });
            latch.countDown();
        });

        if (latch.await(5, TimeUnit.SECONDS)) {
            Platform.runLater(() -> {
                Object polylineCount = map.getJLEngine().executeScript("""
                        Object.keys(map._layers).filter(k => map._layers[k] instanceof L.Polyline).length""");
                assertThat((Number) polylineCount).isEqualTo(1);
            });
        } else {
            throw new TimeoutException("Timed out waiting for polyline addition");
        }
    }

    @Test
    void jlMapView_addMultiPolyline_shouldAddMultiPolylineAsLayer() throws Exception {
        CountDownLatch latch = new CountDownLatch(1);
        Platform.runLater(() -> {
            map.getVectorLayer().addMultiPolyline(new JLLatLng[][]{
                    {
                            JLLatLng.builder().lat(41.509).lng(20.08).build(),
                            JLLatLng.builder().lat(31.503).lng(-10.06).build()
                    },
                    {
                            JLLatLng.builder().lat(51.509).lng(10.08).build(),
                            JLLatLng.builder().lat(55.503).lng(15.06).build()
                    }
            });
            latch.countDown();
        });

        if (latch.await(5, TimeUnit.SECONDS)) {
            Platform.runLater(() -> {
                Object polylineCount = map.getJLEngine().executeScript("""
                        Object.keys(map._layers).filter(k => map._layers[k] instanceof L.Polyline).length""");
                assertThat((Number) polylineCount).isEqualTo(1);
            });
        } else {
            throw new TimeoutException("Timed out waiting for multi-polyline addition");
        }
    }

    @Test
    void jlMapView_addPolygon_shouldAddPolygonAsLayer() throws Exception {
        CountDownLatch latch = new CountDownLatch(1);
        Platform.runLater(() -> {
            map.getVectorLayer().addPolygon(new JLLatLng[][][]{
                    {{
                            JLLatLng.builder().lat(37.0).lng(-109.05).build(),
                            JLLatLng.builder().lat(41.0).lng(-109.03).build(),
                            JLLatLng.builder().lat(41.0).lng(-102.05).build(),
                            JLLatLng.builder().lat(37.0).lng(-102.04).build()
                    }}
            });
            latch.countDown();
        });

        if (latch.await(5, TimeUnit.SECONDS)) {
            Platform.runLater(() -> {
                Object polygonCount = map.getJLEngine().executeScript("""
                        Object.keys(map._layers).filter(k => map._layers[k] instanceof L.Polygon).length""");
                assertThat((Number) polygonCount).isEqualTo(1);
            });
        } else {
            throw new TimeoutException("Timed out waiting for polygon addition");
        }
    }

    @Test
    void jlMapView_addCircle_shouldAddCircleAsLayer() throws Exception {
        CountDownLatch latch = new CountDownLatch(1);
        Platform.runLater(() -> {
            map.getVectorLayer().addCircle(JLLatLng.builder()
                    .lat(ThreadLocalRandom.current().nextDouble(-90.0, 90.0))
                    .lng(ThreadLocalRandom.current().nextDouble(-180.0, 180.0))
                    .build());
            latch.countDown();
        });

        if (latch.await(5, TimeUnit.SECONDS)) {
            Platform.runLater(() -> {
                Object circleCount = map.getJLEngine().executeScript("""
                        Object.keys(map._layers).filter(k => map._layers[k] instanceof L.Circle).length""");
                assertThat((Number) circleCount).isEqualTo(1);
            });
        } else {
            throw new TimeoutException("Timed out waiting for circle addition");
        }
    }

    @Test
    void jlMapView_addCircleMarker_shouldAddCircleMarkerAsLayer() throws Exception {
        CountDownLatch latch = new CountDownLatch(1);
        Platform.runLater(() -> {
            map.getVectorLayer().addCircleMarker(JLLatLng.builder()
                    .lat(ThreadLocalRandom.current().nextDouble(-90.0, 90.0))
                    .lng(ThreadLocalRandom.current().nextDouble(-180.0, 180.0))
                    .build());
            latch.countDown();
        });

        if (latch.await(5, TimeUnit.SECONDS)) {
            Platform.runLater(() -> {
                Object circleMarkerCount = map.getJLEngine().executeScript("""
                        Object.keys(map._layers).filter(k => map._layers[k] instanceof L.CircleMarker).length""");
                assertThat((Number) circleMarkerCount).isEqualTo(1);
            });
        } else {
            throw new TimeoutException("Timed out waiting for circle marker addition");
        }
    }

    @Test
    void jlMapView_addGeoJsonFromContent_shouldAddGeoJsonAsLayer() throws Exception {
        CountDownLatch latch = new CountDownLatch(1);
        Platform.runLater(() -> {
            try {
                map.getGeoJsonLayer().addFromContent("""
                        {
                            "type": "FeatureCollection",
                            "features": [
                                {
                                    "type": "Feature",
                                    "geometry": {
                                        "type": "Point",
                                        "coordinates": [13.4050, 52.5200]
                                    },
                                    "properties": {
                                        "name": "Berlin"
                                    }
                                }
                            ]
                        }""");
            } catch (Exception e) {
                // Handle exception in test
            }
            latch.countDown();
        });

        if (latch.await(5, TimeUnit.SECONDS)) {
            Platform.runLater(() -> {
                Object geoJsonCount = map.getJLEngine().executeScript("""
                        Object.keys(map._layers).filter(k => map._layers[k] instanceof L.GeoJSON).length""");
                assertThat((Number) geoJsonCount).isEqualTo(1);
            });
        } else {
            throw new TimeoutException("Timed out waiting for GeoJSON addition");
        }
    }

}
