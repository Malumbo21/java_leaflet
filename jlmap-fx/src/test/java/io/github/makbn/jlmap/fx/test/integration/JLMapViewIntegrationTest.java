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

    @Test
    void jlMapView_markerWithContextMenu_shouldHaveContextMenu() throws Exception {
        CountDownLatch latch = new CountDownLatch(1);
        final io.github.makbn.jlmap.model.JLMarker[] markerRef = new io.github.makbn.jlmap.model.JLMarker[1];

        Platform.runLater(() -> {
            io.github.makbn.jlmap.model.JLMarker marker = map.getUiLayer().addMarker(
                    JLLatLng.builder()
                            .lat(ThreadLocalRandom.current().nextDouble(-90.0, 90.0))
                            .lng(ThreadLocalRandom.current().nextDouble(-180.0, 180.0))
                            .build(),
                    "Test Marker", false);

            marker.addContextMenu()
                    .addItem("edit", "Edit Marker")
                    .addItem("delete", "Delete Marker")
                    .addItem("info", "Show Info");

            markerRef[0] = marker;
            latch.countDown();
        });

        if (latch.await(5, TimeUnit.SECONDS)) {
            Platform.runLater(() -> {
                io.github.makbn.jlmap.model.JLMarker marker = markerRef[0];
                assertThat(marker.hasContextMenu()).isTrue();
                assertThat(marker.isContextMenuEnabled()).isTrue();
                assertThat(marker.getContextMenu()).isNotNull();
                assertThat(marker.getContextMenu().getItemCount()).isEqualTo(3);
            });
        } else {
            throw new TimeoutException("Timed out waiting for marker with context menu");
        }
    }

    @Test
    void jlMapView_markerContextMenu_shouldSupportEnableDisable() throws Exception {
        CountDownLatch latch = new CountDownLatch(1);
        final io.github.makbn.jlmap.model.JLMarker[] markerRef = new io.github.makbn.jlmap.model.JLMarker[1];

        Platform.runLater(() -> {
            io.github.makbn.jlmap.model.JLMarker marker = map.getUiLayer().addMarker(
                    JLLatLng.builder()
                            .lat(ThreadLocalRandom.current().nextDouble(-90.0, 90.0))
                            .lng(ThreadLocalRandom.current().nextDouble(-180.0, 180.0))
                            .build(),
                    "Test Marker", false);

            marker.addContextMenu()
                    .addItem("edit", "Edit Marker");

            marker.setContextMenuEnabled(false);
            markerRef[0] = marker;
            latch.countDown();
        });

        if (latch.await(5, TimeUnit.SECONDS)) {
            Platform.runLater(() -> {
                io.github.makbn.jlmap.model.JLMarker marker = markerRef[0];
                assertThat(marker.isContextMenuEnabled()).isFalse();

                marker.setContextMenuEnabled(true);
                assertThat(marker.isContextMenuEnabled()).isTrue();
            });
        } else {
            throw new TimeoutException("Timed out waiting for marker context menu enable/disable test");
        }
    }

    @Test
    void jlMapView_markerContextMenu_shouldSupportAddingRemovingItems() throws Exception {
        CountDownLatch latch = new CountDownLatch(1);
        final io.github.makbn.jlmap.model.JLMarker[] markerRef = new io.github.makbn.jlmap.model.JLMarker[1];

        Platform.runLater(() -> {
            io.github.makbn.jlmap.model.JLMarker marker = map.getUiLayer().addMarker(
                    JLLatLng.builder()
                            .lat(ThreadLocalRandom.current().nextDouble(-90.0, 90.0))
                            .lng(ThreadLocalRandom.current().nextDouble(-180.0, 180.0))
                            .build(),
                    "Test Marker", false);

            io.github.makbn.jlmap.element.menu.JLContextMenu<io.github.makbn.jlmap.model.JLMarker> contextMenu =
                    marker.addContextMenu();
            contextMenu.addItem("edit", "Edit Marker")
                    .addItem("delete", "Delete Marker")
                    .addItem("info", "Show Info");

            markerRef[0] = marker;
            latch.countDown();
        });

        if (latch.await(5, TimeUnit.SECONDS)) {
            Platform.runLater(() -> {
                io.github.makbn.jlmap.model.JLMarker marker = markerRef[0];
                io.github.makbn.jlmap.element.menu.JLContextMenu<io.github.makbn.jlmap.model.JLMarker> contextMenu =
                        marker.getContextMenu();

                assertThat(contextMenu.getItemCount()).isEqualTo(3);

                contextMenu.removeItem("edit");
                assertThat(contextMenu.getItemCount()).isEqualTo(2);
                assertThat(contextMenu.getItem("edit")).isNull();

                contextMenu.addItem("new", "New Item");
                assertThat(contextMenu.getItemCount()).isEqualTo(3);
                assertThat(contextMenu.getItem("new")).isNotNull();

                contextMenu.clearItems();
                assertThat(contextMenu.getItemCount()).isZero();
                assertThat(marker.hasContextMenu()).isFalse();
            });
        } else {
            throw new TimeoutException("Timed out waiting for marker context menu operations test");
        }
    }

    @Test
    void jlMapView_markerContextMenu_shouldSupportMenuItemVisibility() throws Exception {
        CountDownLatch latch = new CountDownLatch(1);
        final io.github.makbn.jlmap.model.JLMarker[] markerRef = new io.github.makbn.jlmap.model.JLMarker[1];

        Platform.runLater(() -> {
            io.github.makbn.jlmap.model.JLMarker marker = map.getUiLayer().addMarker(
                    JLLatLng.builder()
                            .lat(ThreadLocalRandom.current().nextDouble(-90.0, 90.0))
                            .lng(ThreadLocalRandom.current().nextDouble(-180.0, 180.0))
                            .build(),
                    "Test Marker", false);

            io.github.makbn.jlmap.element.menu.JLContextMenu<io.github.makbn.jlmap.model.JLMarker> contextMenu =
                    marker.addContextMenu();

            // Add visible item
            contextMenu.addItem(io.github.makbn.jlmap.element.menu.JLMenuItem.builder()
                    .id("visible")
                    .text("Visible Item")
                    .visible(true)
                    .build());

            // Add hidden item
            contextMenu.addItem(io.github.makbn.jlmap.element.menu.JLMenuItem.builder()
                    .id("hidden")
                    .text("Hidden Item")
                    .visible(false)
                    .build());

            markerRef[0] = marker;
            latch.countDown();
        });

        if (latch.await(5, TimeUnit.SECONDS)) {
            Platform.runLater(() -> {
                io.github.makbn.jlmap.model.JLMarker marker = markerRef[0];
                io.github.makbn.jlmap.element.menu.JLContextMenu<io.github.makbn.jlmap.model.JLMarker> contextMenu =
                        marker.getContextMenu();

                assertThat(contextMenu.getItemCount()).isEqualTo(2);
                assertThat(contextMenu.getVisibleItemCount()).isEqualTo(1);
                assertThat(contextMenu.hasVisibleItems()).isTrue();
            });
        } else {
            throw new TimeoutException("Timed out waiting for marker context menu visibility test");
        }
    }

    @Test
    void jlMapView_markerContextMenu_shouldSupportMenuItemUpdate() throws Exception {
        CountDownLatch latch = new CountDownLatch(1);
        final io.github.makbn.jlmap.model.JLMarker[] markerRef = new io.github.makbn.jlmap.model.JLMarker[1];

        Platform.runLater(() -> {
            io.github.makbn.jlmap.model.JLMarker marker = map.getUiLayer().addMarker(
                    JLLatLng.builder()
                            .lat(ThreadLocalRandom.current().nextDouble(-90.0, 90.0))
                            .lng(ThreadLocalRandom.current().nextDouble(-180.0, 180.0))
                            .build(),
                    "Test Marker", false);

            io.github.makbn.jlmap.element.menu.JLContextMenu<io.github.makbn.jlmap.model.JLMarker> contextMenu =
                    marker.addContextMenu();
            contextMenu.addItem("edit", "Edit Marker");

            markerRef[0] = marker;
            latch.countDown();
        });

        if (latch.await(5, TimeUnit.SECONDS)) {
            Platform.runLater(() -> {
                io.github.makbn.jlmap.model.JLMarker marker = markerRef[0];
                io.github.makbn.jlmap.element.menu.JLContextMenu<io.github.makbn.jlmap.model.JLMarker> contextMenu =
                        marker.getContextMenu();

                // Update existing item
                io.github.makbn.jlmap.element.menu.JLMenuItem updatedItem =
                        io.github.makbn.jlmap.element.menu.JLMenuItem.builder()
                                .id("edit")
                                .text("Edit Properties")
                                .icon("https://example.com/edit.png")
                                .build();

                contextMenu.updateItem(updatedItem);

                io.github.makbn.jlmap.element.menu.JLMenuItem item = contextMenu.getItem("edit");
                assertThat(item).isNotNull();
                assertThat(item.getText()).isEqualTo("Edit Properties");
                assertThat(item.getIcon()).isEqualTo("https://example.com/edit.png");
            });
        } else {
            throw new TimeoutException("Timed out waiting for marker context menu update test");
        }
    }

    @Test
    void jlMapView_markerContextMenu_shouldSupportListenerCallback() throws Exception {
        CountDownLatch latch = new CountDownLatch(1);
        final io.github.makbn.jlmap.model.JLMarker[] markerRef = new io.github.makbn.jlmap.model.JLMarker[1];
        final boolean[] listenerInvoked = {false};

        Platform.runLater(() -> {
            io.github.makbn.jlmap.model.JLMarker marker = map.getUiLayer().addMarker(
                    JLLatLng.builder()
                            .lat(ThreadLocalRandom.current().nextDouble(-90.0, 90.0))
                            .lng(ThreadLocalRandom.current().nextDouble(-180.0, 180.0))
                            .build(),
                    "Test Marker", false);

            io.github.makbn.jlmap.element.menu.JLContextMenu<io.github.makbn.jlmap.model.JLMarker> contextMenu =
                    marker.addContextMenu();
            contextMenu.addItem("test", "Test Item")
                    .setOnMenuItemListener(item -> {
                        listenerInvoked[0] = true;
                    });

            markerRef[0] = marker;
            latch.countDown();
        });

        if (latch.await(5, TimeUnit.SECONDS)) {
            Platform.runLater(() -> {
                io.github.makbn.jlmap.model.JLMarker marker = markerRef[0];
                io.github.makbn.jlmap.element.menu.JLContextMenu<io.github.makbn.jlmap.model.JLMarker> contextMenu =
                        marker.getContextMenu();

                // Simulate menu item selection
                io.github.makbn.jlmap.element.menu.JLMenuItem item = contextMenu.getItem("test");
                contextMenu.handleMenuItemSelection(item);

                assertThat(listenerInvoked[0]).isTrue();
            });
        } else {
            throw new TimeoutException("Timed out waiting for marker context menu listener test");
        }
    }

    @Test
    void jlMapView_multipleMarkersWithContextMenu_shouldMaintainIndependentMenus() throws Exception {
        CountDownLatch latch = new CountDownLatch(1);
        final io.github.makbn.jlmap.model.JLMarker[] marker1Ref = new io.github.makbn.jlmap.model.JLMarker[1];
        final io.github.makbn.jlmap.model.JLMarker[] marker2Ref = new io.github.makbn.jlmap.model.JLMarker[1];

        Platform.runLater(() -> {
            io.github.makbn.jlmap.model.JLMarker marker1 = map.getUiLayer().addMarker(
                    JLLatLng.builder()
                            .lat(ThreadLocalRandom.current().nextDouble(-90.0, 90.0))
                            .lng(ThreadLocalRandom.current().nextDouble(-180.0, 180.0))
                            .build(),
                    "Marker 1", false);

            io.github.makbn.jlmap.model.JLMarker marker2 = map.getUiLayer().addMarker(
                    JLLatLng.builder()
                            .lat(ThreadLocalRandom.current().nextDouble(-90.0, 90.0))
                            .lng(ThreadLocalRandom.current().nextDouble(-180.0, 180.0))
                            .build(),
                    "Marker 2", false);

            marker1.addContextMenu()
                    .addItem("edit", "Edit Marker 1")
                    .addItem("delete", "Delete Marker 1");

            marker2.addContextMenu()
                    .addItem("view", "View Marker 2")
                    .addItem("share", "Share Marker 2");

            marker1Ref[0] = marker1;
            marker2Ref[0] = marker2;
            latch.countDown();
        });

        if (latch.await(5, TimeUnit.SECONDS)) {
            Platform.runLater(() -> {
                io.github.makbn.jlmap.model.JLMarker marker1 = marker1Ref[0];
                io.github.makbn.jlmap.model.JLMarker marker2 = marker2Ref[0];

                assertThat(marker1.hasContextMenu()).isTrue();
                assertThat(marker2.hasContextMenu()).isTrue();

                assertThat(marker1.getContextMenu().getItemCount()).isEqualTo(2);
                assertThat(marker2.getContextMenu().getItemCount()).isEqualTo(2);

                assertThat(marker1.getContextMenu().getItem("edit")).isNotNull();
                assertThat(marker1.getContextMenu().getItem("view")).isNull();

                assertThat(marker2.getContextMenu().getItem("view")).isNotNull();
                assertThat(marker2.getContextMenu().getItem("edit")).isNull();
            });
        } else {
            throw new TimeoutException("Timed out waiting for multiple markers context menu test");
        }
    }

    @Test
    void jlMapView_markerContextMenu_withIconsAndVariousStates() throws Exception {
        CountDownLatch latch = new CountDownLatch(1);
        final io.github.makbn.jlmap.model.JLMarker[] markerRef = new io.github.makbn.jlmap.model.JLMarker[1];

        Platform.runLater(() -> {
            io.github.makbn.jlmap.model.JLMarker marker = map.getUiLayer().addMarker(
                    JLLatLng.builder()
                            .lat(ThreadLocalRandom.current().nextDouble(-90.0, 90.0))
                            .lng(ThreadLocalRandom.current().nextDouble(-180.0, 180.0))
                            .build(),
                    "Test Marker", false);

            io.github.makbn.jlmap.element.menu.JLContextMenu<io.github.makbn.jlmap.model.JLMarker> contextMenu =
                    marker.addContextMenu();

            // Item with icon
            contextMenu.addItem(io.github.makbn.jlmap.element.menu.JLMenuItem.builder()
                    .id("edit")
                    .text("Edit")
                    .icon("https://img.icons8.com/material-outlined/24/000000/edit--v1.png")
                    .enabled(true)
                    .visible(true)
                    .build());

            // Disabled item
            contextMenu.addItem(io.github.makbn.jlmap.element.menu.JLMenuItem.builder()
                    .id("delete")
                    .text("Delete")
                    .enabled(false)
                    .visible(true)
                    .build());

            // Hidden item
            contextMenu.addItem(io.github.makbn.jlmap.element.menu.JLMenuItem.builder()
                    .id("admin")
                    .text("Admin Action")
                    .enabled(true)
                    .visible(false)
                    .build());

            markerRef[0] = marker;
            latch.countDown();
        });

        if (latch.await(5, TimeUnit.SECONDS)) {
            Platform.runLater(() -> {
                io.github.makbn.jlmap.model.JLMarker marker = markerRef[0];
                io.github.makbn.jlmap.element.menu.JLContextMenu<io.github.makbn.jlmap.model.JLMarker> contextMenu =
                        marker.getContextMenu();

                assertThat(contextMenu.getItemCount()).isEqualTo(3);
                assertThat(contextMenu.getVisibleItemCount()).isEqualTo(2);

                io.github.makbn.jlmap.element.menu.JLMenuItem editItem = contextMenu.getItem("edit");
                assertThat(editItem.isEnabled()).isTrue();
                assertThat(editItem.isVisible()).isTrue();
                assertThat(editItem.getIcon()).isNotNull();

                io.github.makbn.jlmap.element.menu.JLMenuItem deleteItem = contextMenu.getItem("delete");
                assertThat(deleteItem.isEnabled()).isFalse();
                assertThat(deleteItem.isVisible()).isTrue();

                io.github.makbn.jlmap.element.menu.JLMenuItem adminItem = contextMenu.getItem("admin");
                assertThat(adminItem.isEnabled()).isTrue();
                assertThat(adminItem.isVisible()).isFalse();
            });
        } else {
            throw new TimeoutException("Timed out waiting for marker context menu with various states test");
        }
    }

}
