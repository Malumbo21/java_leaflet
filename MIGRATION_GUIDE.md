# Migration Guide: JavaFX Module (v1.x → v2.0.0)

This guide helps you migrate your JavaFX applications from Java Leaflet v1.x to v2.0.0. The migration is **minimal** and focused - most of your existing code will work without changes.

## 🚀 **What's New in v2.0.0**

- **Multi-Module Architecture**: Clean separation between API, JavaFX, and Vaadin implementations
- **Vaadin Support**: New Vaadin component implementation alongside existing JavaFX support
- **Unified API**: Consistent interface across different UI frameworks
- **Enhanced Modularity**: Better separation of concerns and extensibility
- **Modern Java**: Full Java 17+ and JPMS support

## 🔄 **Migration Overview**

| Component | v1.x | v2.0.0 | Change Required |
|-----------|------|---------|-----------------|
| **Main Class** | `io.github.makbn.jlmap.JLMapView` | `io.github.makbn.jlmap.fx.JLMapView` | ✅ **Yes** |
| **Maven Artifact** | `jlmap` | `jlmap-fx` | ✅ **Yes** |
| **API Classes** | `io.github.makbn.jlmap.*` | `io.github.makbn.jlmap.*` | ❌ **No** |
| **Usage Code** | All existing code | All existing code | ❌ **No** |

## 📋 **Step-by-Step Migration**

### **Step 1: Update Maven Dependency**

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
<dependency>
    <groupId>io.github.makbn</groupId>
    <artifactId>jlmap-fx</artifactId>
    <version>2.0.0</version>
</dependency>
```

### **Step 2: Update Import Statement**

**Before (v1.x):**
```java
import io.github.makbn.jlmap.JLMapView;
```

**After (v2.0.0):**
```java
import io.github.makbn.jlmap.fx.JLMapView;
```

### **Step 3: Update Module Declaration (if using JPMS)**

**Before (v1.x):**
```java
module your.module.name {
    requires io.github.makbn.jlmap;
    // ... other requires
}
```

**After (v2.0.0):**
```java
module your.module.name {
    requires io.github.makbn.jlmap.fx;
    // ... other requires
}
```

## 📖 **Complete Migration Examples**

### **Example 1: Basic Map Setup**

**Before (v1.x):**
```java
import io.github.makbn.jlmap.JLMapView;
import io.github.makbn.jlmap.JLProperties;
import io.github.makbn.jlmap.model.JLLatLng;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class MapExample extends Application {
    
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
        
        stage.setTitle("Java Leaflet Map");
        stage.setScene(scene);
        stage.show();
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}
```

**After (v2.0.0):**
```java
import io.github.makbn.jlmap.fx.JLMapView;  // ← Only this import changes
import io.github.makbn.jlmap.JLProperties;   // ← No change
import io.github.makbn.jlmap.model.JLLatLng; // ← No change
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class MapExample extends Application {
    
    @Override
    public void start(Stage stage) {
        // Create a map view - EXACTLY the same code!
        JLMapView map = JLMapView.builder()
                .jlMapProvider(JLMapProvider.MAP_TILER.parameter(new JLMapOption.Parameter("key", MAP_API_KEY)).build())
                .startCoordinate(JLLatLng.builder()
                        .lat(51.044)
                        .lng(114.07)
                        .build())
                .showZoomController(true)
                .build();
        
        // Create the scene - EXACTLY the same code!
        AnchorPane root = new AnchorPane(map);
        Scene scene = new Scene(root, 800, 600);
        
        stage.setTitle("Java Leaflet Map");
        stage.setScene(scene);
        stage.show();
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}
```

### **Example 2: Advanced Map Usage**

**Before (v1.x):**
```java
import io.github.makbn.jlmap.JLMapView;
import io.github.makbn.jlmap.model.JLLatLng;
import io.github.makbn.jlmap.model.JLOptions;
import io.github.makbn.jlmap.listener.OnJLMapViewListener;

public class AdvancedMapExample {
    
    public void setupMap() {
        JLMapView map = JLMapView.builder()
                .mapType(JLProperties.MapType.OSM_MAPNIK)
                .startCoordinate(new JLLatLng(35.63, 51.45))
                .showZoomController(true)
                .build();
        
        // Add markers
        map.getUiLayer().addMarker(
            new JLLatLng(35.63, 51.45), 
            "Tehran", 
            true
        );
        
        // Add shapes
        map.getVectorLayer().addCircle(
            new JLLatLng(35.63, 51.45), 
            30000, 
            JLOptions.DEFAULT
        );
        
        // Set view
        map.setView(new JLLatLng(10, 10));
        map.getControlLayer().setZoom(5);
    }
}
```

**After (v2.0.0):**
```java
import io.github.makbn.jlmap.fx.JLMapView;  // ← Only this import changes
import io.github.makbn.jlmap.model.JLLatLng; // ← No change
import io.github.makbn.jlmap.model.JLOptions; // ← No change
import io.github.makbn.jlmap.listener.OnJLMapViewListener; // ← No change

public class AdvancedMapExample {
    
    public void setupMap() {
        // EXACTLY the same code!
        JLMapView map = JLMapView.builder()
                .jlMapProvider(JLMapProvider.MAP_TILER.parameter(new JLMapOption.Parameter("key", MAP_API_KEY)).build())
                .startCoordinate(new JLLatLng(35.63, 51.45))
                .showZoomController(true)
                .build();
        
        // EXACTLY the same code!
        map.getUiLayer().addMarker(
            new JLLatLng(35.63, 51.45), 
            "Tehran", 
            true
        );
        
        // EXACTLY the same code!
        map.getVectorLayer().addCircle(
            new JLLatLng(35.63, 51.45), 
            30000, 
            JLOptions.DEFAULT
        );
        
        // EXACTLY the same code!
        map.setView(new JLLatLng(10, 10));
        map.getControlLayer().setZoom(5);
    }
}
```

## 🔍 **What Stays the Same**

✅ **No changes needed for:**

### **API Classes**
- `io.github.makbn.jlmap.JLProperties`
- `io.github.makbn.jlmap.model.*` (JLLatLng, JLOptions, JLColor, etc.)
- `io.github.makbn.jlmap.listener.*` (OnJLMapViewListener, OnJLObjectActionListener, etc.)
- `io.github.makbn.jlmap.layer.leaflet.*` (interfaces)
- `io.github.makbn.jlmap.geojson.*` (GeoJSON support)
- `io.github.makbn.jlmap.exception.*` (exceptions)

### **Functionality**
- Builder pattern usage
- Method calls and API usage
- Event handling and listeners
- Layer management (UI, Vector, Control, GeoJSON)
- Model classes and builders
- Properties and configuration
- Map interactions and controls

## 🏗️ **Project Structure Changes**

### **v1.x (Single Module)**
```
java_leaflet/
├── src/
│   └── main/java/io/github/makbn/jlmap/
│       ├── JLMapView.java          ← Main class
│       ├── JLProperties.java       ← Properties
│       ├── model/                  ← Models
│       ├── layer/                  ← Layers
│       └── listener/               ← Listeners
└── pom.xml
```

### **v2.0.0 (Multi-Module)**
```
java_leaflet/
├── jlmap-parent/                   ← Parent POM
├── jlmap-api/                      ← Core API
│   └── src/main/java/io/github/makbn/jlmap/
│       ├── JLProperties.java       ← Properties (same)
│       ├── model/                  ← Models (same)
│       ├── layer/                  ← Layers (same)
│       └── listener/               ← Listeners (same)
├── jlmap-fx/                       ← JavaFX Implementation
│   └── src/main/java/io/github/makbn/jlmap/fx/
│       └── JLMapView.java          ← Main class (moved here)
├── jlmap-vaadin/                   ← Vaadin Implementation
└── jlmap-vaadin-demo/              ← Vaadin Demo
```

## 🚨 **Common Migration Issues**

### **Issue 1: Class Not Found**
```
Error: cannot find symbol: class JLMapView
```

**Solution:** Update import to `io.github.makbn.jlmap.fx.JLMapView`

### **Issue 2: Module Not Found**
```
Error: module not found: io.github.makbn.jlmap
```

**Solution:** Update module-info.java to `requires io.github.makbn.jlmap.fx;`

### **Issue 3: Maven Dependency Resolution**
```
Error: Could not resolve dependency io.github.makbn:jlmap
```

**Solution:** Change artifactId from `jlmap` to `jlmap-fx`

## 🧪 **Testing Your Migration**

### **1. Build Test**
```bash
mvn clean compile
```

### **2. Runtime Test**
```bash
mvn javafx:run
```

### **3. Module Test (if using JPMS)**
```bash
jar --describe-module --file target/jlmap-fx-2.0.0.jar
```

## 📚 **Additional Resources**

- **API Documentation**: See the `jlmap-api` module for core interfaces
- **JavaFX Examples**: See the `jlmap-fx` module for JavaFX usage
- **Vaadin Examples**: See the `jlmap-vaadin-demo` for Vaadin usage
- **Leaflet Documentation**: [https://leafletjs.com/](https://leafletjs.com/)

## 🎯 **Migration Checklist**

- [ ] Update Maven dependency from `jlmap` to `jlmap-fx`
- [ ] Update import from `io.github.makbn.jlmap.JLMapView` to `io.github.makbn.jlmap.fx.JLMapView`
- [ ] Update module-info.java (if using JPMS) from `requires io.github.makbn.jlmap` to `requires io.github.makbn.jlmap.fx`
- [ ] Test compilation with `mvn clean compile`
- [ ] Test runtime with `mvn javafx:run`
- [ ] Verify all existing functionality works as expected

## 💡 **Pro Tips**

1. **Search and Replace**: Use your IDE's search and replace to update all `JLMapView` imports at once
2. **Incremental Testing**: Test each change individually to isolate any issues
3. **Backup**: Keep a backup of your working v1.x code until migration is complete
4. **Version Control**: Commit your changes incrementally to track progress

## 🤝 **Need Help?**

If you encounter issues during migration:

1. **Check the README**: [README.md](README.md) for comprehensive project information
2. **Review Examples**: Look at the demo applications in `jlmap-fx` and `jlmap-vaadin-demo`
3. **Check Dependencies**: Ensure all required dependencies are properly configured
4. **Verify Java Version**: Ensure you're using Java 17 or higher

---

**Remember**: The migration is designed to be minimal. If you're making extensive changes to your code, you might be doing something wrong. The goal is to change as little as possible while gaining the benefits of the new modular architecture. 