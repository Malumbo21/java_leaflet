package io.github.makbn.jlmap.engine;

import io.github.makbn.jlmap.model.JLObject;

/**
 * Generic bridge for JavaScript-to-Java direct method calls on JLObjects.
 * This allows JavaScript to call Java methods directly on specific object instances.
 *
 * @author Matt Akbarian (@makbn)
 */
public interface JLClientToServerTransporter {

    /**
     * Calls a method on a specific JLObject instance.
     *
     * @param objectId   The unique identifier of the JLObject
     * @param methodName The name of the method to call
     * @param args       Arguments to pass to the method (JSON serialized)
     * @return The result of the method call (JSON serialized), or null for void methods
     */
    String callObjectMethod(String objectId, String methodName, String... args);

    /**
     * Registers a JLObject so it can be called from JavaScript.
     *
     * @param objectId Unique identifier for the object
     * @param object   The JLObject instance
     */
    void registerObject(String objectId, JLObject<?> object);

    /**
     * Unregisters a JLObject.
     *
     * @param objectId The unique identifier of the object to remove
     */
    void unregisterObject(String objectId);

    /**
     * Gets the JavaScript code to inject that enables calling Java methods.
     * This should be executed once to set up the bridge.
     *
     * @return JavaScript code for the bridge
     */
    String getJavaScriptBridge();
}