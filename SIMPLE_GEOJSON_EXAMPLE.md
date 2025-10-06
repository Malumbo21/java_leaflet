# Simple GeoJSON Usage Examples

This document demonstrates the simplified GeoJSON functional approach in Java Leaflet, based on real examples from the
demo applications.

## Basic Usage

### Loading from URL (JavaFX Example)

```java
// Simple GeoJSON from URL with basic styling
JLGeoJson geoJsonObject = map.getGeoJsonLayer()
        .addFromUrl("https://pkgstore.datahub.io/examples/geojson-tutorial/example/data/db696b3bf628d9a273ca9907adcea5c9/example.geojson",
            JLGeoJsonOptions.builder()
                .styleFunction(properties -> JLOptions.builder()
                        .color(JLColor.ORANGE)
                        .weight(2)
                        .fillColor(JLColor.PURPLE)
                        .fillOpacity(0.5)
                        .build())
                .build());
```

### Loading from File (Vaadin Example)

```java
// Simple file loading with default styling
mapView.getGeoJsonLayer().addFromFile(uploadedFile);
```

### Loading US Outline from URL (Vaadin Example)

```java
// Loading a real-world GeoJSON dataset
mapView.getGeoJsonLayer().addFromUrl("https://eric.clst.org/assets/wiki/uploads/Stuff/gz_2010_us_outline_5m.json");
```

## Advanced Styling with Property-Based Functions

### Real-World Styling Example (Vaadin)

```java
// Create GeoJSON options with style function based on feature properties
JLGeoJsonOptions options = JLGeoJsonOptions.builder()
        .styleFunction(features -> JLOptions.builder()
                .fill(true)
                .fillColor(JLColor.fromHex((String) features.get(0).get("fill")))
                .fillOpacity((Double) features.get(0).get("fill-opacity"))
                .stroke(true)
                .color(JLColor.fromHex((String) features.get(0).get("stroke")))
                .build())
        .build();

// Apply to uploaded GeoJSON file
JLGeoJson geoJson = mapView.getGeoJsonLayer().addFromFile(uploadedFile, options);
```

## Functional Filtering with Real Data

### ID-Based Filtering Example (Vaadin)

```java
// Filter features based on their ID (show only even IDs)
JLGeoJsonOptions options = JLGeoJsonOptions.builder()
        .filter(features -> {
            Map<String, Object> featureProperties = features.get(0);
            // Show features with even IDs only
            return ((Integer) featureProperties.get("id")) % 2 == 0;
        })
        .build();

JLGeoJson geoJson = mapView.getGeoJsonLayer().addFromFile(uploadedFile, options);
```

## Combined Styling and Filtering

### Complete Real-World Example (Vaadin)

```java
// Complete example with both styling and filtering
JLGeoJsonOptions options = JLGeoJsonOptions.builder()
        .styleFunction(features -> JLOptions.builder()
                .fill(true)
                .fillColor(JLColor.fromHex((String) features.get(0).get("fill")))
                .fillOpacity((Double) features.get(0).get("fill-opacity"))
                .stroke(true)
                .color(JLColor.fromHex((String) features.get(0).get("stroke")))
                .build())
        .filter(features -> {
            Map<String, Object> featureProperties = features.get(0);
            // Show features with population > 1M, rivers longer than 500km, or parks larger than 100 hectares
            return ((Integer) featureProperties.get("id")) % 2 == 0;
        })
        .build();

// First fly to location to see the features
mapView.getControlLayer().flyTo(new JLLatLng(51.76, -114.06), 5);

JLGeoJson geoJson = mapView.getGeoJsonLayer().addFromFile(uploadedFile, options);
geoJson.setOnActionListener((jlGeoJson, event) ->
        Notification.show("GeoJSON Feature clicked: " + event));
```

## Interactive Examples

### Simple GeoJSON with Click Handler

```java
// Load GeoJSON with click event handling
JLGeoJson geoJson = map.getGeoJsonLayer()
        .addFromUrl("https://your-geojson-url.com/data.json",
            JLGeoJsonOptions.builder()
                .styleFunction(properties -> JLOptions.builder()
                        .color(JLColor.BLUE)
                        .weight(3)
                        .fillOpacity(0.3)
                        .build())
                .build());

// Add click handler to the GeoJSON layer
geoJson.setOnActionListener((source, event) -> {
    System.out.println("GeoJSON clicked: " + event);
});
```

### Context-Aware Navigation

```java
// Fly to specific location before loading GeoJSON
mapView.getControlLayer().flyTo(new JLLatLng(40.7831, -73.9712), 12); // NYC
// Then load relevant GeoJSON data
mapView.getGeoJsonLayer().addFromUrl("https://nyc-geojson-data.com/boroughs.json");
```

## Supported GeoJSON Sources

### 1. From URL

```java
// HTTP/HTTPS URLs
geoJsonLayer.addFromUrl("https://example.com/data.geojson");
```

### 2. From File

```java
// Local file or uploaded file
geoJsonLayer.addFromFile(new File("path/to/data.geojson"));
```

### 3. From String Content

```java
// GeoJSON as string
String geoJsonString = "{ \"type\": \"FeatureCollection\", ... }";
geoJsonLayer.addFromContent(geoJsonString);
```

## Property Access Patterns

When working with feature properties in your styling and filtering functions:

```java
// Access nested properties
Map<String, Object> featureProperties = features.get(0);
String name = (String) featureProperties.get("name");
Integer population = (Integer) featureProperties.get("population");
Double area = (Double) featureProperties.get("area");
String hexColor = (String) featureProperties.get("fill");
Double opacity = (Double) featureProperties.get("fill-opacity");
```

## Real GeoJSON URLs for Testing

These URLs from the demo applications work with the examples above:

1. **Tutorial Example**:
   `https://pkgstore.datahub.io/examples/geojson-tutorial/example/data/db696b3bf628d9a273ca9907adcea5c9/example.geojson`
2. **US Outline**: `https://eric.clst.org/assets/wiki/uploads/Stuff/gz_2010_us_outline_5m.json`

## How It Works

When Leaflet processes your GeoJSON:

1. **Style Function**: For each feature, Leaflet calls the JavaScript function, which proxies back to your Java
   `styleFunction`
2. **Filter Function**: For each feature, Leaflet calls the JavaScript function, which proxies back to your Java
   `filter` predicate
3. **Callback**: Your Java functions receive the feature properties as a `Map<String, Object>` and return the
   appropriate styling or filtering result

This approach gives you:

- Type safety with compile-time checking
- Full Java IDE support (autocomplete, refactoring)
- Simple, readable code
- No need to write JavaScript strings
- Access to real feature properties for dynamic styling
- Interactive event handling capabilities

## Framework-Specific Examples

### JavaFX Implementation

```java
import io.github.makbn.jlmap.fx.JLMapView;

JLMapView map = JLMapView.builder()
        .jlMapProvider(JLMapProvider.OSM_MAPNIK.build())
        .startCoordinate(new JLLatLng(48.864716, 2.349014))
        .build();

JLGeoJson geoJson = map.getGeoJsonLayer().addFromUrl(url, options);
```

### Vaadin Implementation

```java
import io.github.makbn.jlmap.vaadin.JLMapView;

JLMapView mapView = JLMapView.builder()
        .jlMapProvider(JLMapProvider.OSM_MAPNIK.build())
        .startCoordinate(new JLLatLng(48.864716, 2.349014))
        .build();

JLGeoJson geoJson = mapView.getGeoJsonLayer().addFromFile(file, options);
```