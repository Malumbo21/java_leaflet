package io.github.makbn.jlmap.engine;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.makbn.jlmap.model.JLGeoJson;
import io.github.makbn.jlmap.model.JLObject;
import io.github.makbn.jlmap.model.JLOptions;
import lombok.AccessLevel;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

/**
 * Base implementation of the JLObjectBridge that handles method calling logic.
 * Platform-specific implementations extend this to provide the JavaScript integration.
 *
 * @author Matt Akbarian (@makbn)
 */
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public abstract class JLClientToServerTransporterBase<T> implements JLClientToServerTransporter {

    Map<String, JLObject<?>> registeredObjects = new ConcurrentHashMap<>();
    ObjectMapper objectMapper = new ObjectMapper();
    Function<String, T> engineConsumer;

    protected JLClientToServerTransporterBase(Function<String, T> engineConsumer) {
        this.engineConsumer = engineConsumer;
    }

    @NonNull
    protected T execute(String script) {
        return engineConsumer.apply(script);
    }

    @Override
    public void registerObject(String objectId, JLObject<?> object) {
        registeredObjects.put(objectId, object);
        log.debug("Registered object {} of type {}", objectId, object.getClass().getSimpleName());
    }

    @Override
    public void unregisterObject(String objectId) {
        registeredObjects.remove(objectId);
        log.debug("Unregistered object {}", objectId);
    }

    @Override
    public String callObjectMethod(String objectId, String methodName, String... args) {
        try {
            JLObject<?> object = registeredObjects.get(objectId);
            if (object == null) {
                log.warn("No object registered with ID: {}", objectId);
                return null;
            }

            return invokeMethod(object, methodName, args);
        } catch (Exception e) {
            log.error("Error calling method {} on object {}: {}", methodName, objectId, e.getMessage());
            return null;
        }
    }

    private String invokeMethod(JLObject<?> object, String methodName, String... args) throws Exception {
        switch (methodName) {
            case "callStyleFunction" -> {
                if (object instanceof JLGeoJson geoJson && args.length > 0) {
                    List<Map<String, Object>> properties = parsePropertiesMap(args[0]);
                    JLOptions result = geoJson.callStyleFunction(properties);
                    return serializeJLOptions(result);
                }
            }
            case "callFilterFunction" -> {
                if (object instanceof JLGeoJson geoJson && args.length > 0) {
                    List<Map<String, Object>> properties = parsePropertiesMap(args[0]);
                    boolean result = geoJson.callFilterFunction(properties);
                    return String.valueOf(result);
                }
            }
            default -> log.warn("Unknown method: {} on object type: {}", methodName, object.getClass().getSimpleName());
        }
        return null;
    }


    private List<Map<String, Object>> parsePropertiesMap(String jsonProperties) throws JsonProcessingException {
        List<String> jsonStrings = objectMapper.readValue(jsonProperties, new TypeReference<>() {
        });

        return jsonStrings.stream()
                .map(this::convertToMap)
                .toList();
    }

    private Map<String, Object> convertToMap(String item) {
        try {
            return objectMapper.readValue(item, new TypeReference<>() {
            });
        } catch (Exception e) {
            log.error("Error converting object {} to Map: {}", item, e.getMessage());
            return Collections.emptyMap();
        }
    }

    private String serializeJLOptions(JLOptions options) throws JsonProcessingException {
        Map<String, Object> optionsMap = new HashMap<>();

        if (options.getColor() != null) {
            optionsMap.put("color", options.getColor().toHexString());
        }
        if (options.getFillColor() != null) {
            optionsMap.put("fillColor", options.getFillColor().toHexString());
        }
        optionsMap.put("weight", options.getWeight());
        optionsMap.put("opacity", options.getOpacity());
        optionsMap.put("fillOpacity", options.getFillOpacity());
        optionsMap.put("stroke", options.isStroke());
        optionsMap.put("fill", options.isFill());
        optionsMap.put("smoothFactor", options.getSmoothFactor());

        return objectMapper.writeValueAsString(optionsMap);
    }

    @Override
    @SuppressWarnings("all")
    public String getJavaScriptBridge() {
        //language=js
        return """
                // JLObjectBridge - Generic JavaScript-to-Java bridge
                window.jlObjectBridge = {
                    call: async function(objectId, methodName, ...args) {
                        return this._callJava(objectId, methodName, args);
                    },
                
                    _callJava: async function(objectId, methodName, args) {
                        // This will be overridden by platform-specific implementations
                        console.warn('JLObjectBridge not properly initialized for platform');
                        return null;
                    }
                };
                """;
    }
}