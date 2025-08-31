# jlmap Feature Matrix

This document compares the available features and options between the JavaFX and Vaadin versions of the jlmap project.
It is intended to help users and developers understand which features are fully supported, partially supported, or not
available in each version.

| Feature/Option       | JavaFX | Vaadin | Description/Notes                                                               |
|----------------------|:------:|:------:|---------------------------------------------------------------------------------|
| Map Providers        |   ✅    |   ✅    | OSM, MapTiler, etc. supported in both                                           |
| Add Marker           |   ✅    |   ✅    | Both support adding/removing markers                                            |
| Add Popup            |   ✅    |   ✅    | Both support popups                                                             |
| Add Polyline         |   ✅    |   ✅    | Both support polylines                                                          |
| Add MultiPolyline    |   ✅    |   ✅    | Both support multi-polylines                                                    |
| Add Polygon          |   ✅    |   ✅    | Both support polygons (with holes)                                              |
| Add Circle           |   ✅    |   ✅    | Both support circles                                                            |
| Add Circle Marker    |   ✅    |   ✅    | Both support circle markers                                                     |
| Add Image Overlay    |   ✅    |   ✅    | Both support image overlays (JLImageOverlay)                                    |
| Add GeoJSON          |   ✅    |   ✅    | Both support loading GeoJSON from URL, file, or string                          |
| Remove Marker        |   ✅    |   ✅    | Both support removing markers                                                   |
| Remove Popup         |   ✅    |   ✅    | Both support removing popups                                                    |
| Remove Polyline      |   ✅    |   ✅    | Both support removing polylines                                                 |
| Remove Polygon       |   ✅    |   ✅    | Both support removing polygons                                                  |
| Remove Circle        |   ✅    |   ✅    | Both support removing circles                                                   |
| Remove Image Overlay |   ⚠️   |   ⚠️   | JavaFX: Not clearly exposed; Vaadin: not clearly exposed                        |
| Remove GeoJSON       |   ✅    |   ✅    | Both support removing GeoJSON by id                                             |
| Set View/Center      |   ✅    |   ✅    | Both support setting map center                                                 |
| Set Zoom             |   ✅    |   ✅    | Both support set/zoomIn/zoomOut                                                 |
| Fit Bounds           |   ✅    |   ✅    | Both support fitBounds                                                          |
| Fit World            |   ✅    |   ✅    | Both support fitWorld                                                           |
| Pan To               |   ✅    |   ✅    | Both support panTo                                                              |
| Fly To               |   ✅    |   ✅    | Both support flyTo                                                              |
| Event Listeners      |   ✅    |   ✅    | Both support map and object event listeners                                     |
| Custom Map Options   |   ✅    |   ✅    | Both support custom options via JLMapOption/JLOptions                           |
| Layer Control        |   ⚠️   |   ⚠️   | Basic support; advanced layer control (toggle, group) not fully exposed         |
| UI Customization     |   ⚠️   |   ⚠️   | JavaFX: via JavaFX API; Vaadin: via Vaadin API, but not all Leaflet UI features |
| Map Blur/Effects     |   ✅    |   ❌    | JavaFX: supports blur via JavaFX; Vaadin: not available                         |
| Responsive Layout    |   ✅    |   ✅    | Both support responsive layouts                                                 |
| Map Callbacks        |   ✅    |   ✅    | Both support callback handlers                                                  |
| Drag & Drop          |   ✅    |   ✅    | Both support drag events for markers, etc.                                      |
| Tooltip Support      |   ⚠️   |   ⚠️   | Not clearly exposed in API, but possible via custom JS                          |
| Custom Icons         |   ✅    |   ✅    | Both support custom marker icons                                                |
| Z-Index/Layer Order  |   ✅    |   ✅    | Supported via options                                                           |
| Animation            |   ⚠️   |   ⚠️   | JavaFX: possible via JavaFX; Vaadin: limited to Leaflet/JS animations           |
| Print/Export         |   ❌    |   ❌    | Not available out of the box                                                    |

**Legend:**

- ✅ Available
- ⚠️ Partially Available (see Description/Notes)
- ❌ Not Available

This table is based on the current state of the codebase and may change as features are added or improved. For more
details, see the API documentation or source code.

# JLMarker Feature Matrix

| Feature/Option  | JavaFX | Vaadin | Description/Notes                                    |
|-----------------|:------:|:------:|------------------------------------------------------|
| Add Marker      |   ✅    |   ✅    | Both support adding markers                          |
| Remove Marker   |   ✅    |   ✅    | Both support removing markers                        |
| Draggable       |   ✅    |   ✅    | Both support draggable markers                       |
| Custom Icon     |   ✅    |   ✅    | Both support custom marker icons                     |
| Popup/Tooltip   |   ✅    |   ✅    | Both support popups; tooltips possible via custom JS |
| Event Listeners |   ✅    |   ✅    | Click, drag, move, etc.                              |
| Z-Index         |   ✅    |   ✅    | Supported via options                                |
| Animation       |   ⚠️   |   ⚠️   | JavaFX: possible via JavaFX; Vaadin: limited         |

# JLCircle Feature Matrix

| Feature/Option    | JavaFX | Vaadin | Description/Notes             |
|-------------------|:------:|:------:|-------------------------------|
| Add Circle        |   ✅    |   ✅    | Both support adding circles   |
| Remove Circle     |   ✅    |   ✅    | Both support removing circles |
| Set Radius        |   ✅    |   ✅    | Both support setting radius   |
| Set Center        |   ✅    |   ✅    | Both support setting center   |
| Fill/Stroke Color |   ✅    |   ✅    | Both support via options      |
| Opacity           |   ✅    |   ✅    | Both support via options      |
| Event Listeners   |   ✅    |   ✅    | Click, drag, move, etc.       |

# JLCircleMarker Feature Matrix

| Feature/Option       | JavaFX | Vaadin | Description/Notes                    |
|----------------------|:------:|:------:|--------------------------------------|
| Add Circle Marker    |   ✅    |   ✅    | Both support adding circle markers   |
| Remove Circle Marker |   ✅    |   ✅    | Both support removing circle markers |
| Set Radius           |   ✅    |   ✅    | Both support setting radius          |
| Fill/Stroke Color    |   ✅    |   ✅    | Both support via options             |
| Opacity              |   ✅    |   ✅    | Both support via options             |
| Event Listeners      |   ✅    |   ✅    | Click, drag, move, etc.              |

# JLPolyline Feature Matrix

| Feature/Option      | JavaFX | Vaadin | Description/Notes               |
|---------------------|:------:|:------:|---------------------------------|
| Add Polyline        |   ✅    |   ✅    | Both support adding polylines   |
| Remove Polyline     |   ✅    |   ✅    | Both support removing polylines |
| Set Vertices        |   ✅    |   ✅    | Both support setting vertices   |
| Stroke Color/Weight |   ✅    |   ✅    | Both support via options        |
| Opacity             |   ✅    |   ✅    | Both support via options        |
| Event Listeners     |   ✅    |   ✅    | Click, drag, move, etc.         |

# JLMultiPolyline Feature Matrix

| Feature/Option       | JavaFX | Vaadin | Description/Notes                     |
|----------------------|:------:|:------:|---------------------------------------|
| Add MultiPolyline    |   ✅    |   ✅    | Both support adding multi-polylines   |
| Remove MultiPolyline |   ✅    |   ✅    | Both support removing multi-polylines |
| Set Vertices         |   ✅    |   ✅    | Both support setting vertices         |
| Stroke Color/Weight  |   ✅    |   ✅    | Both support via options              |
| Opacity              |   ✅    |   ✅    | Both support via options              |
| Event Listeners      |   ✅    |   ✅    | Click, drag, move, etc.               |

# JLPolygon Feature Matrix

| Feature/Option    | JavaFX | Vaadin | Description/Notes                         |
|-------------------|:------:|:------:|-------------------------------------------|
| Add Polygon       |   ✅    |   ✅    | Both support adding polygons (with holes) |
| Remove Polygon    |   ✅    |   ✅    | Both support removing polygons            |
| Set Vertices      |   ✅    |   ✅    | Both support setting vertices             |
| Fill/Stroke Color |   ✅    |   ✅    | Both support via options                  |
| Opacity           |   ✅    |   ✅    | Both support via options                  |
| Event Listeners   |   ✅    |   ✅    | Click, drag, move, etc.                   |

# JLImageOverlay Feature Matrix

| Feature/Option       | JavaFX | Vaadin | Description/Notes                  |
|----------------------|:------:|:------:|------------------------------------|
| Add Image Overlay    |   ✅    |   ✅    | Both support adding image overlays |
| Remove Image Overlay |   ⚠️   |   ⚠️   | Not clearly exposed in API         |
| Set Bounds           |   ✅    |   ✅    | Both support setting bounds        |
| Set Image URL        |   ✅    |   ✅    | Both support setting image URL     |
| Opacity              |   ✅    |   ✅    | Both support via options           |
| Z-Index              |   ✅    |   ✅    | Both support via options           |
| Event Listeners      |   ❌    |   ❌    | Not available                      |

# JLGeoJson Feature Matrix

| Feature/Option  | JavaFX | Vaadin | Description/Notes                                      |
|-----------------|:------:|:------:|--------------------------------------------------------|
| Add GeoJSON     |   ✅    |   ✅    | Both support loading GeoJSON from URL, file, or string |
| Remove GeoJSON  |   ✅    |   ✅    | Both support removing GeoJSON by id                    |
| Style Features  |   ⚠️   |   ⚠️   | Limited; depends on GeoJSON content and options        |
| Event Listeners |   ⚠️   |   ⚠️   | Limited; depends on implementation                     |
