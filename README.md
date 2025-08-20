# Java Leaflet (JLeaflet)

A Java library for integrating Leaflet maps into Java applications with full Java Platform Module System (JPMS) support. Now supporting both **JavaFX** and **Vaadin** implementations with a unified API.

* Current version: **v2.0.0**

Project Source Code: https://github.com/makbn/java_leaflet

![Java-Leaflet Test](https://github.com/makbn/java_leaflet/blob/master/.github/doc/app.png?raw=true)

> Leaflet is the leading open-source JavaScript library for mobile-friendly interactive maps. Weighing just about 38 KB of JS, it has all the mapping features most developers ever need.
> Leaflet is designed with simplicity, performance and usability in mind. It works efficiently across all major desktop and mobile platforms, can be extended with lots of plugins, has a beautiful, easy to use and well-documented API and a simple, readable source code that is a joy to contribute to.

## 🚀 New in Version 2.0.0

- **Multi-Module Architecture**: Clean separation between API, JavaFX, and Vaadin implementations
- **Vaadin Support**: New Vaadin component implementation alongside existing JavaFX support
- **Unified API**: Consistent interface across different UI frameworks
- **Enhanced Modularity**: Better separation of concerns and extensibility
- **Modern Java**: Full Java 17+ and JPMS support

## 🏗️ Project Structure

This project is now organized as a multi-module Maven project:

```
java_leaflet/
├── jlmap-parent/          # Parent POM
├── jlmap-api/             # Core API and abstractions
├── jlmap-fx/              # JavaFX implementation
├── jlmap-vaadin/          # Vaadin component implementation
└── jlmap-vaadin-demo/     # Vaadin demo application
```

### Module Overview

- **`jlmap-api`**: Core abstractions, interfaces, and models used by all implementations
- **`jlmap-fx`**: JavaFX-specific implementation using WebView
- **`jlmap-vaadin`**: Vaadin component implementation for web applications
- **`jlmap-vaadin-demo`**: Complete Vaadin demo application showcasing the fluent API

## ✨ Features

- **Multi-Framework Support**: JavaFX and Vaadin implementations
- **Java Platform Module System (JPMS) Compatible**: Fully modularized for Java 17+
- **Unified API**: Consistent interface across different UI frameworks
- **Multiple Map Providers**: Support for OpenStreetMap, Mapnik, and other tile providers
- **Interactive Features**: Markers, polygons, polylines, circles, and more
- **Event Handling**: Comprehensive event system for map interactions
- **GeoJSON Support**: Load and display GeoJSON data
- **Customizable**: Extensive customization options for map appearance and behavior
- **Fluent API**: Builder pattern and method chaining for easy configuration

## 📋 Requirements

- **Java**: 17 or higher
- **Maven**: 3.6+ (for building)
- **JavaFX**: 19.0.2.1 or higher (for JavaFX implementation)
- **Vaadin**: 24.8.6 or higher (for Vaadin implementation)

## 🚀 Quick Start

### JavaFX Implementation

Add the JavaFX dependency to your `pom.xml`:

```xml
<dependency>
    <groupId>io.github.makbn</groupId>
    <artifactId>jlmap-fx</artifactId>
    <version>2.0.0</version>
</dependency>
```

### Vaadin Implementation

Add the Vaadin dependency to your `pom.xml`:

```xml
<dependency>
    <groupId>io.github.makbn</groupId>
    <artifactId>jlmap-vaadin</artifactId>
    <version>2.0.0</version>
</dependency>
```

## 📖 Usage Examples

### JavaFX Implementation

```java
import io.github.makbn.jlmap.fx.JLMapView;
import io.github.makbn.jlmap.JLProperties;
import io.github.makbn.jlmap.model.JLLatLng;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class JavaFXMapExample extends Application {
    
    @Override
    public void start(Stage stage) {
        // Create a map view
        JLMapView map = JLMapView.builder()
                .mapType(JLProperties.MapType.OSM_MAPNIK)
                .startCoordinate(JLLatLng.builder()
                        .lat(51.044)
                        .lng(114.07)
                        .build())
                .showZoomController(true)
                .build();
        
        // Create the scene
        AnchorPane root = new AnchorPane(map);
        Scene scene = new Scene(root, 800, 600);
        
        stage.setTitle("Java Leaflet Map (JavaFX)");
        stage.setScene(scene);
        stage.show();
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}
```

### Vaadin Implementation

```java
import io.github.makbn.jlmap.vaadin.JLMapView;
import io.github.makbn.jlmap.JLProperties;
import io.github.makbn.jlmap.model.JLLatLng;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;

@Route("")
public class VaadinMapExample extends VerticalLayout {
    
    public VaadinMapExample() {
        setSizeFull();
        
        // Create a map view
        JLMapView map = JLMapView.builder()
                .mapType(JLProperties.MapType.OSM_MAPNIK)
                .startCoordinate(new JLLatLng(48.864716, 2.349014)) // Paris
                .showZoomController(true)
                .build();
        
        add(map);
        expand(map);
    }
}
```

## 🎯 Core API Features

### Map Control

```java
// Change the current coordinate
map.setView(JLLatLng.builder()
        .lng(10)
        .lat(10)
        .build());

// Map zoom functionalities
map.getControlLayer().setZoom(5);
map.getControlLayer().zoomIn(2);
map.getControlLayer().zoomOut(1);
```

### Adding Markers

```java
// Add a marker to the UI layer
JLMarker marker = map.getUiLayer()
    .addMarker(JLLatLng.builder()
        .lat(35.63)
        .lng(51.45)
        .build(), "Tehran", true);

// Add event listeners
marker.setOnActionListener((jlMarker, event) -> {
    if (event instanceof ClickEvent) {
        System.out.println("Marker clicked: " + jlMarker);
    }
});
```

### Adding Shapes

```java
// Add a circle
map.getVectorLayer()
    .addCircle(JLLatLng.builder()
        .lat(35.63)
        .lng(51.45)
        .build(), 
        30000, 
        JLOptions.builder()
                .color(JLColor.BLACK)
                .build());
```

### Layer Management

The API provides access to different map layers:

- **`map.getUiLayer()`**: UI elements like markers, popups
- **`map.getVectorLayer()`**: Vector graphics (polygons, polylines, circles)
- **`map.getControlLayer()`**: Map controls (zoom, pan, bounds)
- **`map.getGeoJsonLayer()`**: GeoJSON data loading and display

## 🏃‍♂️ Running the Demos

### JavaFX Demo

```bash
cd jlmap-fx
mvn javafx:run
```

### Vaadin Demo

```bash
cd jlmap-vaadin-demo
mvn spring-boot:run
```

Then open your browser to `http://localhost:8080`

## 🔧 Building from Source

### Prerequisites

- Java 17 or higher
- Maven 3.6+
- Node.js (for Vaadin frontend compilation)

### Build Commands

```bash
# Build all modules
mvn clean install

# Build specific module
mvn clean install -pl jlmap-api
mvn clean install -pl jlmap-fx
mvn clean install -pl jlmap-vaadin

# Run tests
mvn test

# Package
mvn package
```

### Module-Aware Building

The project uses Maven's module-aware compilation. Each module has its own `module-info.java` file defining the module structure and dependencies.

## 📦 Module Dependencies

### API Module (`jlmap-api`)

**Exports:**
- `io.github.makbn.jlmap` - Main package
- `io.github.makbn.jlmap.layer` - Layer management
- `io.github.makbn.jlmap.layer.leaflet` - Leaflet-specific layer interfaces
- `io.github.makbn.jlmap.listener` - Event listeners
- `io.github.makbn.jlmap.model` - Data models
- `io.github.makbn.jlmap.exception` - Custom exceptions
- `io.github.makbn.jlmap.geojson` - GeoJSON support
- `io.github.makbn.jlmap.engine` - Web engine abstractions

**Dependencies:**
- SLF4J for logging
- Gson and Jackson for JSON processing
- JetBrains annotations
- JUnit for testing

### JavaFX Module (`jlmap-fx`)

**Dependencies:**
- `jlmap-api` module
- JavaFX modules (controls, base, swing, web, graphics)
- JDK modules (jsobject)

### Vaadin Module (`jlmap-vaadin`)

**Dependencies:**
- `jlmap-api` module
- Vaadin Spring Boot Starter
- Vaadin Core components

## 🔄 Migration from Version 1.x

If you're migrating from version 1.x:

1. **Update Dependencies**: Change from `jlmap` to `jlmap-fx` or `jlmap-vaadin`
2. **Package Updates**: Update imports to use the new module structure
3. **Module Declaration**: Ensure your project has proper module configuration
4. **Build Configuration**: Update Maven configuration for the new dependencies

**📖 [Complete Migration Guide](MIGRATION_GUIDE.md)** - Detailed step-by-step instructions for migrating from v1.x to v2.0.0

### Example Migration

**Before (v1.x):**
```xml
<dependency>
    <groupId>io.github.makbn</groupId>
    <artifactId>jlmap</artifactId>
    <version>1.9.5</version>
</dependency>
```

**After (v2.0.0):**
```xml
<!-- For JavaFX -->
<dependency>
    <groupId>io.github.makbn</groupId>
    <artifactId>jlmap-fx</artifactId>
    <version>2.0.0</version>
</dependency>

<!-- For Vaadin -->
<dependency>
    <groupId>io.github.makbn</groupId>
    <artifactId>jlmap-vaadin</artifactId>
    <version>2.0.0</version>
</dependency>
```

## 🐛 Troubleshooting

### Common Issues

1. **Module Not Found**: Ensure the correct module is in your dependencies
2. **JavaFX Issues**: Verify JavaFX is properly configured for your Java version
3. **Vaadin Issues**: Ensure Node.js is installed for frontend compilation
4. **Lombok Issues**: Verify annotation processing is properly configured

### Module Path Issues

If you encounter module path issues, verify:

```bash
# Check if the module is properly packaged
jar --describe-module --file target/jlmap-fx-2.0.0.jar
jar --describe-module --file target/jlmap-vaadin-2.0.0.jar
```

## Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Ensure all tests pass
5. Submit a pull request

## License

This project is licensed under the GNU General Public License v3.0 - see the [LICENSE](LICENSE) file for details.

## 👨‍💻 Author

**Matt Akbarian** (@makbn)

## Changelog

### Version 2.0.0
- **Major**: Refactored to multi-module Maven project
- **Major**: Added Vaadin component implementation
- **Major**: Separated API from implementations
- **Major**: Enhanced modularity with JPMS support
- **Enhancement**: Unified API across different UI frameworks & Improved Fluent API
- **Enhancement**: Improved separation of concerns
- **Enhancement**: Added comprehensive demo applications
- **Fix**: Resolved module system compatibility issues

### Version 1.9.5
- **Major**: Upgraded to Java Platform Module System (JPMS)
- **Major**: Updated to Java 17 compatibility
- **Major**: Removed internal JavaFX API dependencies
- **Enhancement**: Improved module structure and encapsulation
- **Enhancement**: Updated Maven configuration for module support
- **Fix**: Resolved Lombok annotation processing in module environment

## Roadmap

- [X] Multi-module architecture
- [X] Vaadin implementation
- [X] Unified API design
- [X] Enhanced modularity
- [ ] Additional UI framework implementations (Swing, etc.)
- [ ] Enhanced GeoJSON support
- [ ] Better map provider support
- [ ] SVG support
- [ ] Animation support
- [ ] Performance optimizations

## 📚 Additional Resources

- **API Documentation**: See the `jlmap-api` module for core interfaces
- **JavaFX Examples**: See the `jlmap-fx` module for JavaFX usage
- **Vaadin Examples**: See the `jlmap-vaadin-demo` for Vaadin usage
- **Leaflet Documentation**: [https://leafletjs.com/](https://leafletjs.com/)

---

**Disclaimer**: This project was originally implemented for academic research in geo-visualization. While not actively maintained, it provides a solid foundation for Java-based mapping applications with multiple UI framework support.
