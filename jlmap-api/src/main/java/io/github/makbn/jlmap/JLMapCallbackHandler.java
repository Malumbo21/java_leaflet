package io.github.makbn.jlmap;

import io.github.makbn.jlmap.listener.OnJLMapViewListener;
import io.github.makbn.jlmap.listener.event.*;
import io.github.makbn.jlmap.model.*;
import lombok.AccessLevel;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

/**
 * @author Matt Akbarian  (@makbn)
 */
@Slf4j
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class JLMapCallbackHandler {
    OnJLMapViewListener listener;
    HashMap<Class<? extends JLObject<?>>, HashMap<String, JLObject<?>>> jlObjects;

    HashMap<String, Class<? extends JLObject<?>>[]> classMap;
    Set<JLEventHandler> eventHandlers = Set.of(
            new JLDragEventHandler(),
            new JLInteractionEventHandler(),
            new JLStatusChangeEventHandler(),
            new JLLayerEventHandler()
    );

    public JLMapCallbackHandler(OnJLMapViewListener listener) {
        this.listener = listener;
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
    }

    /**
     * @param functionName name of source function from js
     * @param param1       name of object class
     * @param param2       id of object
     * @param param3       additional param
     * @param param4       additional param
     * @param param5       additional param
     */
    @SuppressWarnings("all")
    public void functionCalled(Object mapView, String functionName, Object param1, Object param2,
                               Object param3, Object param4, Object param5) {
        log.debug("function: {} param1: {} param2: {} param3: {} param4: {} param5: {}",
                functionName, param1, param2, param3, param4, param5);
        try {
            //get target class of Leaflet layer in JL Application
            Class<?>[] targetClasses = classMap.get(param1);
            if (targetClasses == null) {
                targetClasses = classMap.get(param1.toString().replace("jl", ""));
            }
            //function called by an known class
            if (targetClasses != null) {
                //one Leaflet class may map to multiple class in JL Application
                // like ployLine mapped to JLPolyline and JLMultiPolyline
                Arrays.stream(targetClasses)
                        .filter(jlObjects::containsKey)
                        .map(targetClass -> jlObjects.get(targetClass).get(String.valueOf(param2)))
                        .filter(Objects::nonNull)
                        .filter(jlObject -> Objects.nonNull(jlObject.getOnActionListener()))
                        .forEach(jlObject -> {
                            eventHandlers.stream()
                                    .filter(hadler -> hadler.canHandle(functionName))
                                    .forEach(hadler -> hadler.handle(jlObject, functionName,
                                            jlObject.getOnActionListener(), param1, param2, param3, param4, param5));
                        });
            } else if (param1.equals("main_map") && getMapListener().isPresent()) {
                eventHandlers.stream()
                        .filter(hadler -> hadler.canHandle(functionName))
                        .forEach(hadler -> hadler.handle(mapView, functionName, getMapListener().get(),
                                param1, param2, param3, param4, param5));
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
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
            log.error("{} id: {} removed", targetClass.getSimpleName(), object.getId());
        }
    }

    private Optional<OnJLMapViewListener> getMapListener() {
        return Optional.ofNullable(listener);
    }
}
