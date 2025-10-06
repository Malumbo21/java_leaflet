package io.github.makbn.vaadin.demo.views;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.messages.MessageList;
import com.vaadin.flow.component.messages.MessageListItem;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import io.github.makbn.jlmap.map.JLMapProvider;
import io.github.makbn.jlmap.model.*;
import io.github.makbn.jlmap.vaadin.JLMapView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
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
    private static final String MAP_API_KEY = "rNGhTaIpQWWH7C6QGKzF";
    private static final Logger log = LoggerFactory.getLogger(MyTripToCanada.class);
    private static final String TRANSPARENT = "#00FFFFFF";

    // Journey coordinates
    private static final JLLatLng SARI = new JLLatLng(36.5633, 53.0601);
    private static final JLLatLng TEHRAN = new JLLatLng(35.6892, 51.3890);
    private static final JLLatLng DOHA = new JLLatLng(25.2854, 51.5310);
    private static final JLLatLng MONTREAL = new JLLatLng(45.5017, -73.5673);
    private static final JLLatLng CALGARY = new JLLatLng(51.0447, -114.0719);

    // Custom icons for different stages of the journey
    private static final JLIcon CAR_ICON = JLIcon.builder()
            .iconUrl("https://cdn-icons-png.flaticon.com/512/3097/3097220.png")
            .iconSize(new JLPoint(64, 64))
            .iconAnchor(new JLPoint(24, 24))
            .build();

    private static final JLIcon AIRPLANE_ICON = JLIcon.builder()
            .iconUrl("https://cdn-icons-png.flaticon.com/512/3182/3182857.png")
            .iconSize(new JLPoint(64, 64))
            .shadowAnchor(new JLPoint(26, 26))
            .iconAnchor(new JLPoint(24, 24))
            .build();
    private static final JLIcon EAST_AIRPLANE_ICON = JLIcon.builder()
            .iconUrl("https://cdn-icons-png.flaticon.com/512/1058/1058318.png")
            .iconSize(new JLPoint(64, 64))
            .shadowAnchor(new JLPoint(26, 26))
            .iconAnchor(new JLPoint(24, 24))
            .build();

    private static final JLIcon BALLOON = JLIcon.builder()
            .iconUrl("https://cdn-icons-png.flaticon.com/512/1926/1926313.png")
            .iconSize(new JLPoint(64, 64))
            .iconAnchor(new JLPoint(24, 24))
            .build();

    private static final JLIcon BRIEFCASE_ICON = JLIcon.builder()
            .iconUrl("https://cdn-icons-png.flaticon.com/512/5376/5376980.png")
            .iconSize(new JLPoint(64, 64))
            .iconAnchor(new JLPoint(24, 24))
            .build();

    private static final JLIcon DOCUMENT_ICON = JLIcon.builder()
            .iconUrl("https://cdn-icons-png.flaticon.com/512/3127/3127363.png")
            .iconSize(new JLPoint(64, 64))
            .iconAnchor(new JLPoint(24, 24))
            .build();
    private static final JLIcon PASSPORT_ICON = JLIcon.builder()
            .iconUrl("https://cdn-icons-png.flaticon.com/512/18132/18132911.png")
            .iconSize(new JLPoint(64, 64))
            .iconAnchor(new JLPoint(24, 24))
            .build();

    private static final JLIcon HOUSE_ICON = JLIcon.builder()
            .iconUrl("https://cdn-icons-png.flaticon.com/512/3750/3750400.png")
            .iconSize(new JLPoint(64, 64))
            .iconAnchor(new JLPoint(24, 48))
            .build();

    private final List<MessageListItem> messages = new ArrayList<>();
    private final JLMapView mapView;
    private final MessageList messageList;

    private transient JLMarker currentMarker;
    private transient JLPolyline currentPath;

    public MyTripToCanada() {
        setSizeFull();
        setPadding(false);
        setSpacing(false);

        // Main content layout with map
        FlexLayout mainContent = new FlexLayout();
        mainContent.setSizeFull();
        mainContent.getStyle().set("position", "relative");

        // Create the map view
        mapView = JLMapView.builder()
                .jlMapProvider(JLMapProvider.WATER_COLOR
                        .parameter(new JLMapOption.Parameter("key", MAP_API_KEY))
                        .parameter(new JLMapOption.Parameter("initialZoom", "4"))
                        .build())
                .startCoordinate(SARI)
                .showZoomController(false)
                .build();
        mapView.setSizeFull();

        // Create menu overlay (similar to HomeView)
        VerticalLayout menuWrapper = new VerticalLayout();
        menuWrapper.setClassName("jlmap-menu");

        menuWrapper.setPadding(false);
        menuWrapper.setSpacing(false);
        menuWrapper.setWidth(null);
        menuWrapper.setMaxHeight("450px");
        menuWrapper.setMinWidth("280px");
        menuWrapper.setMaxWidth("350px");
        menuWrapper.setHeight(null);
        menuWrapper.getStyle()
                .set("position", "absolute")
                .set("bottom", "20px")
                .set("top", "80% !important")
                .set("left", "20px")
                .set("z-index", "1000")
                .set("background", "rgba(255, 255, 255, 0.95)")
                .set("border-radius", "8px")
                .set("box-shadow", "0 4px 12px rgba(0,0,0,0.15)")
                .set("padding", "16px");

        // Menu content
        VerticalLayout menuContent = new VerticalLayout();
        menuContent.setPadding(false);
        menuContent.setSpacing(false);
        menuContent.setWidthFull();

        // Title
        Span menuTitle = new Span("Java Leaflet: Vaadin");
        menuTitle.getStyle()
                .set("font-size", "1.1em")
                .set("font-weight", "600")
                .set("color", "var(--lumo-header-text-color)")
                .set("display", "block")
                .set("margin-bottom", "12px");

        // Buttons
        Button startButton = new Button("✈️ Start Journey");
        startButton.setWidthFull();
        startButton.getStyle().set("margin-bottom", "8px");
        startButton.addClickListener(e -> {
            startButton.setEnabled(false);
            clearMessages();
            startJourney();
        });

        Button resetButton = new Button("🔄 Reset");
        resetButton.setWidthFull();
        resetButton.addClickListener(e -> {
            resetJourney();
            clearMessages();
            startButton.setEnabled(true);
        });

        Button backButton = new Button("⬅️ Back to Home");
        backButton.setWidthFull();
        backButton.addClickListener(e ->
                UI.getCurrent().navigate(HomeView.class));

        // GitHub footer
        Anchor githubLink = new Anchor("https://github.com/makbn/java_leaflet", "");
        githubLink.setTarget("_blank");
        githubLink.getStyle()
                .set("display", "flex")
                .set("align-items", "center")
                .set("justify-content", "center")
                .set("margin-top", "12px")
                .set("padding-top", "12px")
                .set("border-top", "1px solid var(--lumo-contrast-10pct)")
                .set("color", "var(--lumo-secondary-text-color)")
                .set("text-decoration", "none")
                .set("font-size", "0.875em");
        githubLink.getElement().setProperty("innerHTML",
                "<svg width='16' height='16' viewBox='0 0 16 16' fill='currentColor' style='margin-right: 6px;'><path d='M8 0C3.58 0 0 3.58 0 8c0 3.54 2.29 6.53 5.47 7.59.4.07.55-.17.55-.38 0-.19-.01-.82-.01-1.49-2.01.37-2.53-.49-2.69-.94-.09-.23-.48-.94-.82-1.13-.28-.15-.68-.52-.01-.53.63-.01 1.08.58 1.23.82.72 1.21 1.87.87 2.33.66.07-.52.28-.87.51-1.07-1.78-.2-3.64-.89-3.64-3.95 0-.87.31-1.59.82-2.15-.08-.2-.36-1.01.08-2.12 0 0 .67-.21 2.2.82.64-.18 1.32-.27 2-.27.68 0 1.36.09 2 .27 1.53-1.04 2.2-.82 2.2-.82.44 1.11.16 1.92.08 2.12.51.56.82 1.27.82 2.15 0 3.07-1.87 3.75-3.65 3.95.29.25.54.73.54 1.48 0 1.07-.01 1.93-.01 2.19 0 .21.15.46.55.38A8.013 8.013 0 0 0 16 8c0-4.42-3.58-8-8-8z'/></svg>" +
                        "<span>View on GitHub</span>");

        menuContent.add(menuTitle, startButton, resetButton, backButton, githubLink);
        menuWrapper.add(menuContent);

        // Message list panel
        VerticalLayout messagePanel = new VerticalLayout();
        messagePanel.setWidth("380px");
        messagePanel.getStyle()
                .set("border-left", "1px solid var(--lumo-contrast-10pct)");
        messagePanel.setPadding(false);
        messagePanel.setSpacing(false);

        H3 messageTitle = new H3("📝 Journey Log");
        messageTitle.getStyle()
                .set("margin", "0")
                .set("padding", "var(--lumo-space-m)");

        messageList = new MessageList();
        messageList.setItems(messages);

        messagePanel.add(messageTitle, messageList);
        messagePanel.expand(messageList);

        // Add components to main content
        mainContent.add(mapView, messagePanel);
        mainContent.expand(mapView);

        // Add menu overlay on top of map
        mapView.getElement().appendChild(menuWrapper.getElement());

        add(mainContent);
    }

    private void startJourney() {
        log.info("Starting journey animation");
        resetJourney();

        addMessage("🎬", "Journey begins! Buckle up for an adventure!", "Driver");
        log.info("About to animate first segment: Sari to Tehran");

        // Step 1: Car from Sari to Tehran (3 seconds)
        animateSegment(
                SARI,
                TEHRAN,
                CAR_ICON,
                "#FF5722",
                3000,
                7,
                "Sari, Iran",
                "Tehran, Iran",
                () -> {
                    addMessage("🚗", "Departed from beautiful Sari! Driving through scenic routes to Tehran...", "Driver");
                    // Step 2: Briefcase and passport (1.5 seconds)
                    addMessage("🏙️", "Arrived in Tehran! Time to pack my bags and grab my passport!", "Driver");
                    showTransition(TEHRAN, BRIEFCASE_ICON, 1500, () -> {
                        // Step 3: Airplane Tehran to Doha (4 seconds)
                        addMessage("✈️", "Taking off from Tehran! Soaring through the clouds to Doha...", "Qatar Airways");
                        animateSegment(
                                TEHRAN,
                                DOHA,
                                AIRPLANE_ICON,
                                "#2196F3",
                                4000,
                                5,
                                "Tehran, Iran",
                                "Doha, Qatar",
                                () -> {
                                    // Step 4: Transit in Doha (1.5 seconds)
                                    addMessage("🛬", "Landed in Doha! Quick layover for coffee and passport check ☕", "Doha Airport");
                                    showTransition(DOHA, PASSPORT_ICON, 1500, () -> {
                                        // Step 5: Airplane Doha to Montreal (5 seconds)
                                        addMessage("🌍", "Crossing the Atlantic! Long flight ahead but Canada awaits! 🇨🇦", "Qatar Airways");
                                        animateSegment(
                                                DOHA,
                                                MONTREAL,
                                                EAST_AIRPLANE_ICON,
                                                "#2196F3",
                                                5000,
                                                3,
                                                "Doha, Qatar",
                                                "Montreal, Canada",
                                                () -> {
                                                    // Step 6: Customs in Montreal (1.5 seconds)
                                                    addMessage("🍁", "Bonjour Montreal! Going through customs and immigration...", "YUL Airport");
                                                    showTransition(MONTREAL, DOCUMENT_ICON, 1500, () -> {
                                                        // Step 7: Domestic flight to Calgary (4 seconds)
                                                        addMessage("🛫", "Domestic flight time! Heading west to the Rockies!", "Air Canada");
                                                        animateSegment(
                                                                MONTREAL,
                                                                CALGARY,
                                                                BALLOON,
                                                                "#E91E63",
                                                                8000,
                                                                6,
                                                                "Montreal, Canada",
                                                                "Calgary, Canada",
                                                                () -> {
                                                                    // Step 8: Arrived in Calgary
                                                                    addMessage("🏠", "FINALLY HOME in Calgary! What an amazing journey! 🎉", "YYC Airport");
                                                                    showTransition(CALGARY, HOUSE_ICON, 2000, () -> {
                                                                        addMessage("🎊", "Journey complete! Time to mow your lawn and shovel the snow! 🏔️", "HOA manager");
                                                                        Notification.show("🎉 Welcome to Calgary, Canada! Journey Complete!",
                                                                                5000,
                                                                                Notification.Position.TOP_CENTER);
                                                                    });
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
                                int duration, int zoomLevel, String departureName, String destinationName,
                                Runnable onComplete) {
        log.info("Animating segment from {} to {} with icon {}", start, end, icon);
        // Fly to show the route
        JLLatLng midPoint = new JLLatLng(
                (start.getLat() + end.getLat()) / 2,
                (start.getLng() + end.getLng()) / 2
        );
        log.info("Flying to midpoint: {}", midPoint);
        mapView.getControlLayer().flyTo(midPoint, zoomLevel);

        // Add popup at departure
        JLPopup departurePopup = mapView.getUiLayer().addPopup(start,
                "<div style='font-weight: bold; color: #FF5722;'>📍 Departure: " + departureName + "</div>");

        // Remove previous path if exists
        if (currentPath != null) {
            log.info("Removing previous path");
            currentPath.remove();
        }

        // Create animated path with more points for smoother animation (10x more)
        JLLatLng[] pathPoints = createCurvedPath(start, end, 300);
        log.info("Created path with {} points", pathPoints.length);


        // Add popup at destination
        JLPopup destinationPopup = mapView.getUiLayer().addPopup(end,
                "<div style='font-weight: bold; color: #4CAF50;'>🎯 Destination: " + destinationName + "</div>");

        // Animate marker along path
        log.info("Starting marker animation");
        UI.getCurrent().push();
        animateMarkerAlongPath(icon, pathPoints, pathColor, duration, () -> {
            // Remove popups after animation
            departurePopup.remove();
            destinationPopup.remove();

            // Call the original onComplete callback
            if (onComplete != null) {
                onComplete.run();
            }
        });
    }

    private void animateMarkerAlongPath(JLIcon icon, JLLatLng[] path, String pathColor, int duration, Runnable onComplete) {
        UI ui = UI.getCurrent();

        // Increase animation steps to 200 for smoother animation (10x more than before)
        int totalSteps = Math.min(200, path.length);
        int delayPerStep = duration / totalSteps;

        log.info("Animating marker with icon {} along {} steps, delay per step: {}ms", icon, totalSteps, delayPerStep);

        // Create the marker once at starting position
        if (currentMarker == null) {
            currentMarker = mapView.getUiLayer().addMarker(path[0], null, false);
            currentMarker.setIcon(icon);
        } else {
            currentMarker.setLatLng(path[0]);
            currentMarker.setIcon(icon);
        }

        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
        AtomicInteger currentStep = new AtomicInteger(0);

        executor.scheduleAtFixedRate(() -> {
            int step = currentStep.getAndIncrement();
            if (step == 1) {
                ui.access(() -> {
                    currentPath = mapView.getVectorLayer().addPolyline(
                            path,
                            JLOptions.DEFAULT.toBuilder()
                                    .fillColor(JLColor.fromHex(TRANSPARENT))
                                    .color(JLColor.fromHex(pathColor))
                                    .stroke(true)
                                    .fill(false)
                                    .weight(4)
                                    .opacity(0.7)
                                    .build()
                    );
                    log.info("Added polyline to map");
                });
            }

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
        }, 1000, delayPerStep, TimeUnit.MILLISECONDS);
    }

    private void showTransition(JLLatLng position, JLIcon icon, int duration, Runnable onComplete) {
        UI ui = UI.getCurrent();

        log.info("Showing transition at {} with icon {}", position, icon);

        // Just update the marker position and icon, don't remove
        if (currentMarker == null) {
            currentMarker = mapView.getUiLayer().addMarker(position, null, false);
            currentMarker.setIcon(icon);
        } else {
            currentMarker.setLatLng(position);
            currentMarker.setIcon(icon);
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

            // Bezier point
            double lat = Math.pow(1 - t, 2) * start.getLat() +
                    2 * (1 - t) * t * control.getLat() +
                    Math.pow(t, 2) * end.getLat();
            double lng = Math.pow(1 - t, 2) * start.getLng() +
                    2 * (1 - t) * t * control.getLng() +
                    Math.pow(t, 2) * end.getLng();

            // Bezier derivative (tangent vector)
            double dLat = 2 * (1 - t) * (control.getLat() - start.getLat()) +
                    2 * t * (end.getLat() - control.getLat());
            double dLng = 2 * (1 - t) * (control.getLng() - start.getLng()) +
                    2 * t * (end.getLng() - control.getLng());

            // Perpendicular vector (normal)
            double normalLat = -dLng;
            double normalLng = dLat;

            // Normalize the normal vector
            double length = Math.sqrt(normalLat * normalLat + normalLng * normalLng);
            if (length != 0) {
                normalLat /= length;
                normalLng /= length;
            }
            double distance = start.distanceTo(end) / 100000; // Adjust for map scale
            // Add a sinusoidal offset for wiggle
            double wiggleAmplitude = 0.2 * Math.log(distance); // Adjust amplitude
            double wiggleFrequency = 1.5 * Math.log(distance);    // Number of wiggles along the path
            double wiggle = Math.sin(t * Math.PI * wiggleFrequency) * wiggleAmplitude;

            double finalLat = lat + normalLat * wiggle;
            double finalLng = lng + normalLng * wiggle;

            path[i] = new JLLatLng(finalLat, finalLng);
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

    private void addMessage(String icon, String message, String username) {
        UI.getCurrent().access(() -> {
            MessageListItem item = new MessageListItem(
                    icon + " " + message,
                    Instant.now(),
                    username
            );

            messages.add(item);
            messageList.setItems(messages);
        });
    }

    private void clearMessages() {
        messages.clear();
        messageList.setItems(messages);
    }
}
