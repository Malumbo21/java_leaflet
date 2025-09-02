package io.github.makbn.jlmap.fx.layer;

import io.github.makbn.jlmap.JLMapCallbackHandler;
import io.github.makbn.jlmap.JLProperties;
import io.github.makbn.jlmap.engine.JLTransporter;
import io.github.makbn.jlmap.engine.JLWebEngine;
import io.github.makbn.jlmap.layer.leaflet.LeafletVectorLayerInt;
import io.github.makbn.jlmap.listener.JLAction;
import io.github.makbn.jlmap.model.*;
import io.github.makbn.jlmap.model.builder.*;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Represents the Vector layer on Leaflet map.
 *
 * @author Matt Akbarian  (@makbn)
 */
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class JLVectorLayer extends JLLayer implements LeafletVectorLayerInt {
    AtomicInteger idGenerator;
    JLTransporter transporter;

    public JLVectorLayer(JLWebEngine<Object> engine, JLMapCallbackHandler callbackHandler) {
        super(engine, callbackHandler);
        this.idGenerator = new AtomicInteger();
        this.transporter = () -> transport -> {
            // NO-OP
            return null;
        };
    }

    /**
     * Drawing polyline overlays on the map with {@link JLOptions#DEFAULT}
     * options
     *
     * @see JLVectorLayer#addPolyline(JLLatLng[], JLOptions)
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
        String elementUniqueName = getElementUniqueName(JLGeoJson.class, idGenerator.incrementAndGet());
        JLPolylineBuilder builder = new JLPolylineBuilder()
                .setUuid(elementUniqueName)
                .withOptions(options)
                .addLatLngs(Arrays.stream(vertices).map(latLng -> new double[]{latLng.getLat(), latLng.getLng()}).toList())
                .withCallbacks(jlCallbackBuilder -> {
                    //TODO register all possible callbacks
                })
                .setTransporter(transporter);
        engine.executeScript(builder.buildJsElement());
        JLPolyline polyline = builder.buildJLObject();
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
        engine.executeScript(removeLayerWithUUID(id));

        callbackHandler.remove(JLPolyline.class, id);
        callbackHandler.remove(JLMultiPolyline.class, id);
        return true;
    }

    /**
     * Drawing multi polyline overlays on the map with
     * {@link JLOptions#DEFAULT} options.
     *
     * @return the added {@link JLMultiPolyline}  to map
     * @see JLVectorLayer#addMultiPolyline(JLLatLng[][], JLOptions)
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
    public JLMultiPolyline addMultiPolyline(JLLatLng[][] vertices, JLOptions options) {
        String elementUniqueName = getElementUniqueName(JLGeoJson.class, idGenerator.incrementAndGet());
        JLMultiPolylineBuilder builder = new JLMultiPolylineBuilder()
                .setUuid(elementUniqueName)
                .withOptions(options)
                .withCallbacks(jlCallbackBuilder -> {
                    //TODO register all possible callbacks
                })
                .setTransporter(transporter);
        for (JLLatLng[] group : vertices) {
            List<double[]> groupList = new ArrayList<>();
            for (JLLatLng v : group) {
                groupList.add(new double[]{v.getLat(), v.getLng()});
            }
            builder.addLine(groupList);
        }
        engine.executeScript(builder.buildJsElement());
        JLMultiPolyline multiPolyline = builder.buildJLObject();
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
        engine.executeScript(removeLayerWithUUID(id));
        callbackHandler.remove(JLMultiPolyline.class, id);
        return true;
    }

    /**
     * Drawing polygon overlays on the map with {@link JLOptions#DEFAULT}
     * options.
     *
     * @see JLVectorLayer#addPolygon(JLLatLng[][][], JLOptions)
     */
    @Override
    public JLPolygon addPolygon(JLLatLng[][][] vertices, JLOptions options) {
        String elementUniqueName = getElementUniqueName(JLGeoJson.class, idGenerator.incrementAndGet());
        JLPolygonBuilder builder = new JLPolygonBuilder()
                .setUuid(elementUniqueName)
                .withOptions(options)
                .withCallbacks(jlCallbackBuilder -> {
                    //TODO register all possible callbacks
                })
                .setTransporter(transporter);
        for (JLLatLng[][] group : vertices) {
            List<double[]> groupList = new ArrayList<>();
            for (JLLatLng[] ring : group) {
                for (JLLatLng v : ring) {
                    groupList.add(new double[]{v.getLat(), v.getLng()});
                }
            }
            builder.addLatLngGroup(groupList);
        }
        engine.executeScript(builder.buildJsElement());
        JLPolygon polygon = builder.buildJLObject();
        callbackHandler.addJLObject(elementUniqueName, polygon);
        return polygon;
    }

    /**
     * Drawing polygon overlays on the map with {@link JLOptions#DEFAULT}
     * options.
     *
     * @see JLVectorLayer#addPolygon(JLLatLng[][][], JLOptions)
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
        String result = engine.executeScript(removeLayerWithUUID(id)).toString();

        callbackHandler.remove(JLPolygon.class, id);

        return Boolean.parseBoolean(result);
    }

    /**
     * Drawing circle overlays on the map.
     *
     * @param center  center point of circle
     * @param radius  radius of circle in meters
     * @param options see {@link JLOptions} for customizing
     * @return the added {@link JLCircle}  to map
     */
    @Override
    public JLCircle addCircle(JLLatLng center, int radius, JLOptions options) {
        // TODO move theses to JLOPtions
        String hexColor = convertColorToString(options.getColor());
        String fillHexColor = convertColorToString(options.getFillColor());

        var elementUniqueName = getElementUniqueName(JLCircle.class, idGenerator.incrementAndGet());

        var circleBuilder = new JLCircleBuilder()
                .setUuid(elementUniqueName)
                .setLat(center.getLat())
                .setLng(center.getLng())
                .setRadius(radius)
                .setTransporter(transporter)
                .withOptions(options)
                .withCallbacks(jlCallbackBuilder -> {
                    jlCallbackBuilder.on(JLAction.MOVE);
                    jlCallbackBuilder.on(JLAction.ADD);
                    jlCallbackBuilder.on(JLAction.REMOVE);
                });

        engine.executeScript(circleBuilder.buildJsElement());
        var circle = circleBuilder.buildJLObject();
        callbackHandler.addJLObject(elementUniqueName, circle);
        return circle;
    }

    /**
     * Drawing circle overlays on the map with {@link JLOptions#DEFAULT}
     * options.
     *
     * @see JLVectorLayer#addCircle(JLLatLng, int, JLOptions)
     */
    @Override
    public JLCircle addCircle(JLLatLng center) {
        return addCircle(center, 1000, JLOptions.DEFAULT);
    }

    /**
     * Remove a circle from the map by id.
     *
     * @param id of circle
     * @return {@link Boolean#TRUE} if removed successfully
     */
    @Override
    public boolean removeCircle(String id) {
        String result = engine.executeScript(removeLayerWithUUID(id)).toString();

        callbackHandler.remove(JLCircle.class, id);

        return Boolean.parseBoolean(result);
    }

    /**
     * Drawing circle marker overlays on the map.
     *
     * @param center  center point of circle marker
     * @param radius  radius of circle marker in pixels
     * @param options see {@link JLOptions} for customizing
     * @return the added {@link JLCircleMarker}  to map
     */
    @Override
    public JLCircleMarker addCircleMarker(JLLatLng center, int radius, JLOptions options) {
        //TODO mov theses to JLOPtions
        String hexColor = convertColorToString(options.getColor());
        String fillHexColor = convertColorToString(options.getFillColor());
        var elementUniqueName = getElementUniqueName(JLCircleMarker.class, idGenerator.incrementAndGet());

        var circleMarkerBuilder = new JLCircleMarkerBuilder()
                .setUuid(elementUniqueName)
                .setLat(center.getLat())
                .setLng(center.getLng())
                .setRadius(radius)
                .setTransporter(transporter)
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
     * @see JLVectorLayer#addCircleMarker(JLLatLng, int, JLOptions)
     */
    @Override
    public JLCircleMarker addCircleMarker(JLLatLng center) {
        return addCircleMarker(center, JLProperties.DEFAULT_CIRCLE_MARKER_RADIUS, JLOptions.DEFAULT);
    }

    /**
     * Remove a circle marker from the map by id.
     *
     * @param id of circle marker
     * @return {@link Boolean#TRUE} if removed successfully
     */
    @Override
    public boolean removeCircleMarker(String id) {
        String result = engine.executeScript(removeLayerWithUUID(id)).toString();

        callbackHandler.remove(JLCircleMarker.class, id);

        return Boolean.parseBoolean(result);
    }

    private String convertJLLatLngToString(JLLatLng[] latLngs) {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        for (int i = 0; i < latLngs.length; i++) {
            sb.append("[").append(latLngs[i].getLat()).append(", ")
                    .append(latLngs[i].getLng()).append("]");
            if (i < latLngs.length - 1) {
                sb.append(", ");
            }
        }
        sb.append("]");
        return sb.toString();
    }

    private String convertJLLatLngToString(JLLatLng[][] latLngsList) {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        for (int i = 0; i < latLngsList.length; i++) {
            sb.append(convertJLLatLngToString(latLngsList[i]));
            if (i < latLngsList.length - 1) {
                sb.append(", ");
            }
        }
        sb.append("]");
        return sb.toString();
    }

    private String convertJLLatLngToString(JLLatLng[][][] latLngList) {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        for (int i = 0; i < latLngList.length; i++) {
            sb.append(convertJLLatLngToString(latLngList[i]));
            if (i < latLngList.length - 1) {
                sb.append(", ");
            }
        }
        sb.append("]");
        return sb.toString();
    }

    private String convertColorToString(JLColor c) {
        return c.toHexString();
    }
}
