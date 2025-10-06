# Java Leaflet (JLeaflet) - Vaadin Implementation

This module provides a Vaadin implementation of the Java Leaflet API. It allows you to easily integrate Leaflet maps into your Vaadin applications while maintaining a consistent API with other implementations.

## Features

- Seamless integration with Vaadin Flow
- Consistent API with other JLeaflet implementations
- Support for markers, popups, and other map elements
- Event handling for map interactions

## Requirements

- Java 17 or higher
- Vaadin 24.3.5 or higher

## Usage

### Maven Dependency

```xml
<dependency>
    <groupId>io.github.makbn</groupId>
    <artifactId>jlmap-vaadin</artifactId>
    <version>2.0.0</version>
</dependency>
```

### Basic Example

```java
// Create a map view
JLMapView mapView = JLMapView.builder()
        .JLMapProvider(JLProperties.MapType.OSM)
        .startCoordinate(new JLLatLng(48.864716, 2.349014)) // Paris
        .showZoomController(true)
        .build();

// Add the map to your layout
layout.add(mapView);
layout.setSizeFull();
mapView.setSizeFull();

// Add a marker
JLMarker marker = JLMarker.builder()
        .latLng(new JLLatLng(48.864716, 2.349014))
        .popup("Hello, Paris!")
        .build();
mapView.getUiLayer().addMarker(marker);

// Add a click listener to the marker
marker.setActionListener(new OnJLObjectActionListener() {
    @Override
    public void onClick(ClickEvent event) {
        Notification.show("Marker clicked!");
    }
});
```

In some cases adding map directly to `HtmlContainer` or `com.vaadin.flow.component.html.Main` deos not work as expected.
In such cases, you can wrap the map in a `com.vaadin.flow.component.orderedlayout.*` or as an example `VerticalLayout`!
Read more
here: [Vaadin Examples!](https://github.com/makbn/java_leaflet/wiki/Examples-and-Tutorials#vaadin-implementation).

```java

### Map Events

You can listen for map events by implementing the `OnJLMapViewListener` interface:

```java
public class MyView extends VerticalLayout implements OnJLMapViewListener {
    
    public MyView() {
        JLMapView mapView = JLMapView.builder()
                .JLMapProvider(JLProperties.MapType.OSM)
                .startCoordinate(new JLLatLng(48.864716, 2.349014))
                .showZoomController(true)
                .build();
        mapView.setMapViewListener(this);
        add(mapView);
    }
    
    @Override
    public void mapLoadedSuccessfully(JLMapController mapController) {
        Notification.show("Map loaded successfully!");
    }
}
```

## Demo Application

A demo application is included in the `io.github.makbn.jlmap.vaadin.demo` package. You can run it to see the Vaadin implementation in action.