package io.github.makbn.jlmap.vaadin.layer;

import com.vaadin.flow.component.page.PendingJavaScriptResult;
import io.github.makbn.jlmap.JLMapEventHandler;
import io.github.makbn.jlmap.JLProperties;
import io.github.makbn.jlmap.engine.JLWebEngine;
import io.github.makbn.jlmap.layer.leaflet.LeafletVectorLayerInt;
import io.github.makbn.jlmap.listener.JLAction;
import io.github.makbn.jlmap.model.*;
import io.github.makbn.jlmap.model.builder.*;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Represents the Vector layer on Leaflet map.
 *
 * @author Matt Akbarian  (@makbn)
 */
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class JLVaadinVectorLayer extends JLVaadinLayer implements LeafletVectorLayerInt {
    AtomicInteger idGenerator;

    public JLVaadinVectorLayer(JLWebEngine<PendingJavaScriptResult> engine,
                               JLMapEventHandler callbackHandler) {
        super(engine, callbackHandler);
        this.idGenerator = new AtomicInteger();
    }

    /**
     * Drawing polyline overlays on the map with {@link JLOptions#DEFAULT}
     * options
     *
     * @see JLVaadinVectorLayer#addPolyline(JLLatLng[], JLOptions)
     */
    @Override
    public JLPolyline addPolyline(JLLatLng[] vertices) {
        return addPolyline(vertices, JLOptions.DEFAULT);
    }

    /**
     * Drawing polyline overlays on the map.
     *
     * @param vertices arrays of LatLng points
     * @param options  see {@link JLOptions} for customizing
     * @return the added {@link JLPolyline}  to map
     */
    @Override
    public JLPolyline addPolyline(JLLatLng[] vertices, JLOptions options) {
        var elementUniqueName = getElementUniqueName(JLPolyline.class, idGenerator.incrementAndGet());

        var polylineBuilder = new JLPolylineBuilder()
                .setUuid(elementUniqueName)
                .setTransporter(getTransporter())
                .withOptions(options)
                .withCallbacks(jlCallbackBuilder -> {
                    jlCallbackBuilder.on(JLAction.ADD);
                    jlCallbackBuilder.on(JLAction.REMOVE);
                    jlCallbackBuilder.on(JLAction.CLICK);
                    jlCallbackBuilder.on(JLAction.DOUBLE_CLICK);
                });

        // Add vertices to the builder using the correct method
        for (JLLatLng vertex : vertices) {
            polylineBuilder.addLatLng(vertex.getLat(), vertex.getLng());
        }

        engine.executeScript(polylineBuilder.buildJsElement());
        var polyline = polylineBuilder.buildJLObject();
        callbackHandler.addJLObject(elementUniqueName, polyline);
        return polyline;
    }

    /**
     * Remove a polyline from the map by id.
     *
     * @param id of polyline
     * @return {@link Boolean#TRUE} if removed successfully
     */
    @Override
    public boolean removePolyline(String id) {
        try {
            engine.executeScript(removeLayerWithUUID(id));
            callbackHandler.remove(JLPolyline.class, id);
        } catch (RuntimeException e) {
            log.error(e.getMessage(), e);
            return false;
        }
        return true;
    }

    /**
     * Drawing multi polyline overlays on the map with
     * {@link JLOptions#DEFAULT} options.
     *
     * @return the added {@link JLMultiPolyline}  to map
     * @see JLVaadinVectorLayer#addMultiPolyline(JLLatLng[][], JLOptions)
     */
    @Override
    public JLMultiPolyline addMultiPolyline(JLLatLng[][] vertices) {
        return addMultiPolyline(vertices, JLOptions.DEFAULT);
    }

    /**
     * Drawing MultiPolyline shape overlays on the map with
     * multi-dimensional array.
     *
     * @param vertices arrays of LatLng points
     * @param options  see {@link JLOptions} for customizing
     * @return the added {@link JLMultiPolyline}  to map
     */
    @Override
    public JLMultiPolyline addMultiPolyline(JLLatLng[][] vertices,
                                            JLOptions options) {
        var elementUniqueName = getElementUniqueName(JLMultiPolyline.class, idGenerator.incrementAndGet());

        var multiPolylineBuilder = new JLMultiPolylineBuilder()
                .setUuid(elementUniqueName)
                .setTransporter(getTransporter())
                .withOptions(options)
                .withCallbacks(jlCallbackBuilder -> {
                    jlCallbackBuilder.on(JLAction.ADD);
                    jlCallbackBuilder.on(JLAction.REMOVE);
                    jlCallbackBuilder.on(JLAction.CLICK);
                    jlCallbackBuilder.on(JLAction.DOUBLE_CLICK);
                });

        // Add vertices arrays to the builder using the correct method
        for (JLLatLng[] vertexArray : vertices) {
            List<double[]> line = new ArrayList<>();
            for (JLLatLng vertex : vertexArray) {
                line.add(new double[]{vertex.getLat(), vertex.getLng()});
            }
            multiPolylineBuilder.addLine(line);
        }

        engine.executeScript(multiPolylineBuilder.buildJsElement());
        var multiPolyline = multiPolylineBuilder.buildJLObject();
        callbackHandler.addJLObject(elementUniqueName, multiPolyline);
        return multiPolyline;
    }

    /**
     * Remove a multi polyline from the map by id.
     *
     * @param id of multi polyline
     * @return {@link Boolean#TRUE} if removed successfully
     */
    @Override
    public boolean removeMultiPolyline(String id) {
        try {
            engine.executeScript(removeLayerWithUUID(id));
            callbackHandler.remove(JLMultiPolyline.class, id);
        } catch (RuntimeException e) {
            log.error(e.getMessage(), e);
            return false;
        }
        return true;
    }

    /**
     * Drawing polygon overlays on the map with {@link JLOptions#DEFAULT}
     * options.
     *
     * @see JLVaadinVectorLayer#addPolygon(JLLatLng[][][], JLOptions)
     */
    @Override
    public JLPolygon addPolygon(JLLatLng[][][] vertices, JLOptions options) {
        var elementUniqueName = getElementUniqueName(JLPolygon.class, idGenerator.incrementAndGet());

        var polygonBuilder = new JLPolygonBuilder()
                .setUuid(elementUniqueName)
                .setTransporter(getTransporter())
                .withOptions(options)
                .withCallbacks(jlCallbackBuilder -> {
                    jlCallbackBuilder.on(JLAction.ADD);
                    jlCallbackBuilder.on(JLAction.REMOVE);
                    jlCallbackBuilder.on(JLAction.CLICK);
                    jlCallbackBuilder.on(JLAction.DOUBLE_CLICK);
                });

        // Add vertices arrays to the builder using the correct method
        for (JLLatLng[][] ringArray : vertices) {
            List<double[]> group = new ArrayList<>();
            for (JLLatLng[] ring : ringArray) {
                for (JLLatLng vertex : ring) {
                    group.add(new double[]{vertex.getLat(), vertex.getLng()});
                }
            }
            polygonBuilder.addLatLngGroup(group);
        }

        engine.executeScript(polygonBuilder.buildJsElement());
        var polygon = polygonBuilder.buildJLObject();
        callbackHandler.addJLObject(elementUniqueName, polygon);
        return polygon;
    }

    /**
     * Drawing polygon overlays on the map with {@link JLOptions#DEFAULT}
     * options.
     *
     * @see JLVaadinVectorLayer#addPolygon(JLLatLng[][][], JLOptions)
     */
    @Override
    public JLPolygon addPolygon(JLLatLng[][][] vertices) {
        return addPolygon(vertices, JLOptions.DEFAULT);
    }

    /**
     * Remove a polygon from the map by id.
     *
     * @param id of polygon
     * @return {@link Boolean#TRUE} if removed successfully
     */
    @Override
    public boolean removePolygon(String id) {
        try {
            engine.executeScript(removeLayerWithUUID(id));
            callbackHandler.remove(JLPolygon.class, id);
        } catch (RuntimeException e) {
            log.error(e.getMessage(), e);
            return false;
        }
        return true;
    }

    /**
     * Drawing circle overlays on the map.
     *
     * @param center  latLng point of circle
     * @param radius  radius of circle in meters
     * @param options see {@link JLOptions} for customizing
     * @return the added {@link JLCircle}  to map
     */
    @Override
    public JLCircle addCircle(JLLatLng center, int radius, JLOptions options) {
        var elementUniqueName = getElementUniqueName(JLCircle.class, idGenerator.incrementAndGet());

        var circleBuilder = new JLCircleBuilder()
                .setUuid(elementUniqueName)
                .setLat(center.getLat())
                .setLng(center.getLng())
                .setRadius(radius)
                .setTransporter(getTransporter())
                .withOptions(options)
                .withCallbacks(jlCallbackBuilder -> {
                    jlCallbackBuilder.on(JLAction.MOVE);
                    jlCallbackBuilder.on(JLAction.ADD);
                    jlCallbackBuilder.on(JLAction.REMOVE);
                    jlCallbackBuilder.on(JLAction.CLICK);
                    jlCallbackBuilder.on(JLAction.DOUBLE_CLICK);
                });

        engine.executeScript(circleBuilder.buildJsElement());
        var circle = circleBuilder.buildJLObject();
        callbackHandler.addJLObject(elementUniqueName, circle);
        return circle;
    }

    /**
     * Drawing circle overlays on the map with {@link JLOptions#DEFAULT}
     * options and {@link JLProperties#DEFAULT_CIRCLE_RADIUS}.
     *
     * @see JLVaadinVectorLayer#addCircle(JLLatLng, int, JLOptions)
     */
    @Override
    public JLCircle addCircle(JLLatLng center) {
        return addCircle(center, JLProperties.DEFAULT_CIRCLE_RADIUS, JLOptions.DEFAULT);
    }

    /**
     * Remove a circle from the map by id.
     *
     * @param id of circle
     * @return {@link Boolean#TRUE} if removed successfully
     */
    @Override
    public boolean removeCircle(String id) {
        try {
            engine.executeScript(removeLayerWithUUID(id));
            callbackHandler.remove(JLCircle.class, id);
        } catch (RuntimeException e) {
            log.error(e.getMessage(), e);
            return false;
        }
        return true;
    }

    /**
     * Drawing circle marker overlays on the map.
     *
     * @param center  latLng point of circle marker
     * @param radius  radius of circle marker in pixels
     * @param options see {@link JLOptions} for customizing
     * @return the added {@link JLCircleMarker}  to map
     */
    @Override
    public JLCircleMarker addCircleMarker(JLLatLng center, int radius,
                                          JLOptions options) {
        var elementUniqueName = getElementUniqueName(JLCircleMarker.class, idGenerator.incrementAndGet());

        var circleMarkerBuilder = new JLCircleMarkerBuilder()
                .setUuid(elementUniqueName)
                .setLat(center.getLat())
                .setLng(center.getLng())
                .setRadius(radius)
                .setTransporter(getTransporter())
                .withOptions(options)
                .withCallbacks(jlCallbackBuilder -> {
                    jlCallbackBuilder.on(JLAction.MOVE);
                    jlCallbackBuilder.on(JLAction.ADD);
                    jlCallbackBuilder.on(JLAction.REMOVE);
                    jlCallbackBuilder.on(JLAction.CLICK);
                    jlCallbackBuilder.on(JLAction.DOUBLE_CLICK);
                });

        engine.executeScript(circleMarkerBuilder.buildJsElement());
        var circleMarker = circleMarkerBuilder.buildJLObject();
        callbackHandler.addJLObject(elementUniqueName, circleMarker);
        return circleMarker;
    }

    /**
     * Drawing circle marker overlays on the map with {@link JLOptions#DEFAULT}
     * options.
     *
     * @see JLVaadinVectorLayer#addCircleMarker(JLLatLng, int, JLOptions)
     */
    @Override
    public JLCircleMarker addCircleMarker(JLLatLng center) {
        return addCircleMarker(center, 6, JLOptions.DEFAULT);
    }

    /**
     * Remove a circle marker from the map by id.
     *
     * @param id of circle marker
     * @return {@link Boolean#TRUE} if removed successfully
     */
    @Override
    public boolean removeCircleMarker(String id) {
        try {
            engine.executeScript(removeLayerWithUUID(id));
            callbackHandler.remove(JLCircleMarker.class, id);
        } catch (RuntimeException e) {
            log.error(e.getMessage(), e);
            return false;
        }
        return true;
    }
}
