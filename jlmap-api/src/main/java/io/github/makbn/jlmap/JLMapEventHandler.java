package io.github.makbn.jlmap;

import io.github.makbn.jlmap.listener.event.*;
import io.github.makbn.jlmap.model.*;
import lombok.AccessLevel;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Objects;
import java.util.Set;

/**
 * @author Matt Akbarian  (@makbn)
 */
@Slf4j
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class JLMapEventHandler {
    public static final String MAP_TYPE = "map";
    public static final String MAP_UUID = "main_map";

    HashMap<Class<? extends JLObject<?>>, HashMap<String, JLObject<?>>> jlObjects;

    HashMap<String, Class<? extends JLObject<?>>[]> classMap;
    Set<JLEventHandler> eventHandlers = Set.of(
            new JLDragEventHandler(),
            new JLInteractionEventHandler(),
            new JLStatusChangeEventHandler(),
            new JLLayerEventHandler()
    );

    public JLMapEventHandler() {
        this.jlObjects = new HashMap<>();
        this.classMap = new HashMap<>();
        initClassMap();
    }

    @SuppressWarnings("unchecked")
    private void initClassMap() {
        classMap.put(JLMarker.class.getSimpleName().toLowerCase(), new Class[]{JLMarker.class});
        classMap.put(JLPopup.class.getSimpleName().toLowerCase(), new Class[]{JLPopup.class});
        classMap.put(JLCircleMarker.class.getSimpleName().toLowerCase(), new Class[]{JLCircleMarker.class});
        classMap.put(JLCircle.class.getSimpleName().toLowerCase(), new Class[]{JLCircle.class});
        classMap.put(JLPolyline.class.getSimpleName().toLowerCase(), new Class[]{JLPolyline.class});
        classMap.put(JLMultiPolyline.class.getSimpleName().toLowerCase(), new Class[]{JLMultiPolyline.class});
        classMap.put(JLPolygon.class.getSimpleName().toLowerCase(), new Class[]{JLPolygon.class});
        classMap.put(JLGeoJson.class.getSimpleName().toLowerCase(), new Class[]{JLGeoJson.class});
    }

    /**
     * @param functionName name of source function from js
     * @param jlType       name of object class
     * @param uuid         id of object
     * @param param1       additional param
     * @param param2       additional param
     * @param param3       additional param
     */
    @SuppressWarnings("all")
    public void functionCalled(JLMap<?> mapView, String functionName, Object jlType, Object uuid,
                               Object param1, Object param2, Object param3) {
        log.debug("function: {} jlType: {} uuid: {} param1: {} param2: {} param3: {}",
                functionName, jlType, uuid, param1, param2, param3);
        try {
            //get target class of Leaflet layer in JL Application
            Class<?>[] targetClasses = classMap.get(jlType);
            if (targetClasses == null) {
                targetClasses = classMap.get(jlType.toString().replace("jl", ""));
            }
            //function called by an known class
            if (targetClasses != null) {
                //one Leaflet class may map to multiple class in JL Application
                // like ployLine mapped to JLPolyline and JLMultiPolyline
                Arrays.stream(targetClasses)
                        .filter(jlObjects::containsKey)
                        .map(targetClass -> jlObjects.get(targetClass).get(String.valueOf(uuid)))
                        .filter(Objects::nonNull)
                        .filter(jlObject -> Objects.nonNull(jlObject.getOnActionListener()))
                        .forEach(jlObject -> invokeEventHandler(functionName, jlType, uuid, param1, param2, param3, mapView, jlObject));
            } else if (MAP_TYPE.equals(jlType) && MAP_UUID.equals(uuid) && mapView.getOnActionListener() != null) {
                eventHandlers.stream()
                        .filter(hadler -> hadler.canHandle(functionName))
                        .forEach(hadler -> hadler.handle(mapView, mapView, functionName, mapView.getOnActionListener(),
                                jlType, uuid, param1, param2, param3));
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    private void invokeEventHandler(String functionName, Object jlType, Object uuid, Object param1, Object param2, Object param3, JLMap<?> map, JLObject<?> jlObject) {
        eventHandlers.stream()
                .filter(handler -> handler.canHandle(functionName))
                .forEach(handler -> handler.handle(map, jlObject, functionName,
                        jlObject.getOnActionListener(), jlType, uuid, param1, param2, param3));
    }

    public void addJLObject(@NonNull String key, @NonNull JLObject<?> object) {
        if (jlObjects.containsKey(object.getClass())) {
            jlObjects.get(object.getClass())
                    .put(key, object);
        } else {
            HashMap<String, JLObject<?>> map = new HashMap<>();
            map.put(key, object);
            //noinspection unchecked
            jlObjects.put((Class<? extends JLObject<?>>) object.getClass(), map);
        }
    }

    public void remove(@NonNull Class<? extends JLObject<?>> targetClass, @NonNull String key) {
        if (!jlObjects.containsKey(targetClass))
            return;
        JLObject<?> object = jlObjects.get(targetClass).remove(key);
        if (object != null) {
            log.error("{} id: {} removed", targetClass.getSimpleName(), object.getJLId());
        }
    }
}
