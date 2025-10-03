package io.github.makbn.vaadin.demo.views;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import io.github.makbn.jlmap.map.JLMapProvider;
import io.github.makbn.jlmap.model.*;
import io.github.makbn.jlmap.vaadin.JLMapView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * My Trip to Canada - An animated journey visualization
 *
 * @author Matt Akbarian  (@makbn)
 */

@Route("my-trip-to-canada")
public class MyTripToCanada extends VerticalLayout {
    public static final String MAP_API_KEY = "rNGhTaIpQWWH7C6QGKzF";
    private static final Logger log = LoggerFactory.getLogger(MyTripToCanada.class);

    // Journey coordinates
    private final JLLatLng SARI = new JLLatLng(36.5633, 53.0601);
    private final JLLatLng TEHRAN = new JLLatLng(35.6892, 51.3890);
    private final JLLatLng DOHA = new JLLatLng(25.2854, 51.5310);
    private final JLLatLng MONTREAL = new JLLatLng(45.5017, -73.5673);
    private final JLLatLng CALGARY = new JLLatLng(51.0447, -114.0719);

    // Custom icons for different stages of the journey
    private final JLIcon CAR_ICON = JLIcon.builder()
            .iconUrl("https://cdn-icons-png.flaticon.com/512/3097/3097220.png")
            .iconSize(new JLPoint(64, 64))
            .iconAnchor(new JLPoint(24, 24))
            .build();

    private final JLIcon AIRPLANE_ICON = JLIcon.builder()
            .iconUrl("https://cdn-icons-png.flaticon.com/512/3182/3182857.png")
            .iconSize(new JLPoint(64, 64))
            .shadowAnchor(new JLPoint(26, 26))
            .iconAnchor(new JLPoint(24, 24))
            .build();
    private final JLIcon EAST_AIRPLANE_ICON = JLIcon.builder()
            .iconUrl("https://cdn-icons-png.flaticon.com/512/1058/1058318.png")
            .iconSize(new JLPoint(64, 64))
            .shadowAnchor(new JLPoint(26, 26))
            .iconAnchor(new JLPoint(24, 24))
            .build();

    private final JLIcon RED_AIRPLANE_ICON = JLIcon.builder()
            .iconUrl("https://cdn-icons-png.flaticon.com/512/1077/1077903.png")
            .iconSize(new JLPoint(64, 64))
            .iconAnchor(new JLPoint(24, 24))
            .build();

    private final JLIcon BRIEFCASE_ICON = JLIcon.builder()
            .iconUrl("https://cdn-icons-png.flaticon.com/512/5376/5376980.png")
            .iconSize(new JLPoint(64, 64))
            .iconAnchor(new JLPoint(24, 24))
            .build();

    private final JLIcon DOCUMENT_ICON = JLIcon.builder()
            .iconUrl("https://cdn-icons-png.flaticon.com/512/3127/3127363.png")
            .iconSize(new JLPoint(64, 64))
            .iconAnchor(new JLPoint(24, 24))
            .build();
    private final JLIcon PASSPORT_ICON = JLIcon.builder()
            .iconUrl("https://cdn-icons-png.flaticon.com/512/18132/18132911.png")
            .iconSize(new JLPoint(64, 64))
            .iconAnchor(new JLPoint(24, 24))
            .build();

    private final JLIcon HOUSE_ICON = JLIcon.builder()
            .iconUrl("https://cdn-icons-png.flaticon.com/512/3750/3750400.png")
            .iconSize(new JLPoint(64, 64))
            .iconAnchor(new JLPoint(24, 48))
            .build();

    private JLMapView mapView;
    private JLMarker currentMarker;
    private JLPolyline currentPath;

    public MyTripToCanada() {
        setSizeFull();

        // Title
        H1 title = new H1("🌍 My Trip to Canada");

        // Create the map view
        mapView = JLMapView.builder()
                .jlMapProvider(JLMapProvider.WATER_COLOR
                        .parameter(new JLMapOption.Parameter("key", MAP_API_KEY))
                        .parameter(new JLMapOption.Parameter("initialZoom", "4"))
                        .build())
                .startCoordinate(SARI)
                .showZoomController(true)
                .build();

        // Control panel
        HorizontalLayout controlPanel = new HorizontalLayout();
        controlPanel.setSpacing(true);
        controlPanel.setHeight("280px");

        Button startButton = new Button("✈️ Start Journey");
        startButton.addClickListener(e -> {
            startButton.setEnabled(false);
            startJourney();
        });

        Button resetButton = new Button("🔄 Reset");

        resetButton.addClickListener(e -> {
            resetJourney();
            startButton.setEnabled(true);
        });

        controlPanel.add(title, startButton, resetButton);

        add(controlPanel);
        addAndExpand(mapView);
    }

    private void startJourney() {
        log.info("Starting journey animation");
        resetJourney();

        Notification.show("Starting journey from Sari, Iran...", 2000, Notification.Position.BOTTOM_CENTER);
        log.info("About to animate first segment: Sari to Tehran");

        // Step 1: Car from Sari to Tehran (5 seconds)
        animateSegment(
                SARI,
                TEHRAN,
                CAR_ICON,
                "#FF5722",
                3000,
                7,
                () -> {
                    // Step 2: Briefcase and passport (1 second)
                    Notification.show("Arriving in Tehran - Getting ready to fly...", 2000, Notification.Position.BOTTOM_CENTER);
                    showTransition(TEHRAN, BRIEFCASE_ICON, 1500, () -> {
                        // Step 3: Airplane Tehran to Doha (3 seconds)
                        Notification.show("Flying to Doha...", 2000, Notification.Position.BOTTOM_CENTER);
                        animateSegment(
                                TEHRAN,
                                DOHA,
                                AIRPLANE_ICON,
                                "#2196F3",
                                4000,
                                5,
                                () -> {
                                    // Step 4: Change airplane animation (same position)
                                    Notification.show("Transit in Doha...", 2000, Notification.Position.BOTTOM_CENTER);
                                    showTransition(DOHA, PASSPORT_ICON, 1500, () -> {
                                        // Step 5: Airplane Doha to Montreal (5 seconds)
                                        Notification.show("Flying to Montreal, Canada...", 2000, Notification.Position.BOTTOM_CENTER);
                                        animateSegment(
                                                DOHA,
                                                MONTREAL,
                                                EAST_AIRPLANE_ICON,
                                                "#2196F3",
                                                5000,
                                                3,
                                                () -> {
                                                    // Step 6: Paper document (1 second)
                                                    Notification.show("Customs in Montreal...", 2000, Notification.Position.BOTTOM_CENTER);
                                                    showTransition(MONTREAL, DOCUMENT_ICON, 1500, () -> {
                                                        // Step 7: Red airplane Montreal to Calgary (4 seconds)
                                                        Notification.show("Domestic flight to Calgary...", 2000, Notification.Position.BOTTOM_CENTER);
                                                        animateSegment(
                                                                MONTREAL,
                                                                CALGARY,
                                                                RED_AIRPLANE_ICON,
                                                                "#E91E63",
                                                                4000,
                                                                6,
                                                                () -> {
                                                                    // Step 8: House at Calgary
                                                                    showTransition(CALGARY, HOUSE_ICON, 2000, () ->
                                                                            Notification.show("🎉 Welcome to Calgary, Canada! Journey Complete!",
                                                                                    5000,
                                                                                    Notification.Position.TOP_CENTER)
                                                                    );
                                                                }
                                                        );
                                                    });
                                                }
                                        );
                                    });
                                }
                        );
                    });
                }
        );
    }

    private void animateSegment(JLLatLng start, JLLatLng end, JLIcon icon, String pathColor,
                                int duration, int zoomLevel, Runnable onComplete) {
        log.info("Animating segment from {} to {} with icon {}", start, end, icon);

        // Remove previous path if exists
        if (currentPath != null) {
            log.info("Removing previous path");
            currentPath.remove();
        }

        // Create animated path with more points for smoother animation (10x more)
        JLLatLng[] pathPoints = createCurvedPath(start, end, 300);
        log.info("Created path with {} points", pathPoints.length);

        currentPath = mapView.getVectorLayer().addPolyline(
                pathPoints,
                JLOptions.DEFAULT.toBuilder()
                        .color(JLColor.fromHex(pathColor))
                        .fillColor(JLColor.fromHex("#00FFFFFF"))
                        .color(JLColor.fromHex("#00FFFFFF"))
                        .fill(false)
                        .weight(4)
                        .opacity(0.7)
                        .build()
        );
        log.info("Added polyline to map");

        // Fly to show the route
        JLLatLng midPoint = new JLLatLng(
                (start.getLat() + end.getLat()) / 2,
                (start.getLng() + end.getLng()) / 2
        );
        log.info("Flying to midpoint: {}", midPoint);
        mapView.getControlLayer().flyTo(midPoint, zoomLevel);

        // Animate marker along path
        log.info("Starting marker animation");
        UI.getCurrent().push();
        animateMarkerAlongPath(icon, pathPoints, duration, onComplete);
    }

    private void animateMarkerAlongPath(JLIcon icon, JLLatLng[] path, int duration, Runnable onComplete) {
        UI ui = UI.getCurrent();

        // Increase animation steps to 200 for smoother animation (10x more than before)
        int totalSteps = Math.min(200, path.length);
        int delayPerStep = duration / totalSteps;

        log.info("Animating marker with icon {} along {} steps, delay per step: {}ms", icon, totalSteps, delayPerStep);

        // Create the marker once at starting position
        if (currentMarker == null) {
            currentMarker = mapView.getUiLayer().addMarker(path[0], null, false);
            setMarkerIconDirect(currentMarker, icon);
        } else {
            currentMarker.setLatLng(path[0]);
            setMarkerIconDirect(currentMarker, icon);
        }

        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
        AtomicInteger currentStep = new AtomicInteger(0);

        executor.scheduleAtFixedRate(() -> {
            int step = currentStep.getAndIncrement();

            if (step <= totalSteps) {
                // Calculate which point in the path to use
                int pathIndex = (step * (path.length - 1)) / totalSteps;
                JLLatLng position = path[pathIndex];

                ui.access(() -> {
                    try {
                        if (currentMarker != null) {
                            log.debug("Step {}/{}: Moving marker to position {}", step, totalSteps, position);
                            currentMarker.setLatLng(position);
                            UI.getCurrent().push();
                        }
                    } catch (Exception e) {
                        log.error("Error moving marker at step {}: {}", step, e.getMessage(), e);
                    }
                });
            } else {
                log.info("Animation complete, shutting down executor");
                executor.shutdown();
                if (onComplete != null) {
                    ui.access(onComplete::run);
                }
            }
        }, 0, delayPerStep, TimeUnit.MILLISECONDS);
    }

    private void showTransition(JLLatLng position, JLIcon icon, int duration, Runnable onComplete) {
        UI ui = UI.getCurrent();

        log.info("Showing transition at {} with icon {}", position, icon);

        // Just update the marker position and icon, don't remove
        if (currentMarker == null) {
            currentMarker = mapView.getUiLayer().addMarker(position, null, false);
            setMarkerIconDirect(currentMarker, icon);
        } else {
            currentMarker.setLatLng(position);
            setMarkerIconDirect(currentMarker, icon);
        }

        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
        executor.schedule(() -> {
            ui.access(() -> {
                if (onComplete != null) {
                    onComplete.run();
                }
            });
            executor.shutdown();
        }, duration, TimeUnit.MILLISECONDS);
    }

    private JLLatLng[] createCurvedPath(JLLatLng start, JLLatLng end, int points) {
        JLLatLng[] path = new JLLatLng[points];

        // Create a smooth curved path using quadratic bezier curve
        double midLat = (start.getLat() + end.getLat()) / 2;
        double midLng = (start.getLng() + end.getLng()) / 2;

        // Add curvature
        double offsetLat = (end.getLng() - start.getLng()) * 0.2;
        double offsetLng = -(end.getLat() - start.getLat()) * 0.2;

        JLLatLng control = new JLLatLng(midLat + offsetLat, midLng + offsetLng);

        for (int i = 0; i < points; i++) {
            double t = (double) i / (points - 1);
            double lat = Math.pow(1 - t, 2) * start.getLat() +
                    2 * (1 - t) * t * control.getLat() +
                    Math.pow(t, 2) * end.getLat();
            double lng = Math.pow(1 - t, 2) * start.getLng() +
                    2 * (1 - t) * t * control.getLng() +
                    Math.pow(t, 2) * end.getLng();
            path[i] = new JLLatLng(lat, lng);
        }

        return path;
    }

    private void resetJourney() {
        if (currentMarker != null) {
            currentMarker.remove();
            currentMarker = null;
        }
        if (currentPath != null) {
            currentPath.remove();
            currentPath = null;
        }

        // Reset view to starting position
        mapView.getControlLayer().flyTo(SARI, 4);
    }

    /**
     * Helper method to set marker icon using direct JavaScript execution
     * This bypasses the toString() issue with JLIcon parameter serialization
     */
    private void setMarkerIconDirect(JLMarker marker, JLIcon icon) {
        String iconScript = String.format("""
                        var icon = L.icon({
                            iconUrl: '%s',
                            iconSize: [%d, %d],
                            iconAnchor: [%d, %d]
                        });
                        this.%s.setIcon(icon);
                        """,
                icon.getIconUrl(),
                (int) icon.getIconSize().getX(),
                (int) icon.getIconSize().getY(),
                (int) icon.getIconAnchor().getX(),
                (int) icon.getIconAnchor().getY(),
                marker.getJLId()
        );

        mapView.getElement().executeJs(iconScript);
        log.debug("Set icon for marker {} using direct JS", marker.getJLId());
    }
}
