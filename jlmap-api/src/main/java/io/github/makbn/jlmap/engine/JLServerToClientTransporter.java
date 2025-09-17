package io.github.makbn.jlmap.engine;

import io.github.makbn.jlmap.exception.JLException;

import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

/**
 * Core transport interface for executing JavaScript operations from Java server-side code.
 * <p>
 * This interface defines the communication bridge between Java objects and the JavaScript
 * Leaflet map running in a web view or browser. It enables Java code to execute JavaScript
 * functions, modify map elements, and retrieve results asynchronously.
 * </p>
 * <h3>Architecture Overview:</h3>
 * <p>
 * The transporter operates as a functional interface that:
 * </p>
 * <ul>
 *   <li><strong>Sends Commands</strong>: Translates Java method calls to JavaScript execution</li>
 *   <li><strong>Handles Results</strong>: Converts JavaScript return values back to Java objects</li>
 *   <li><strong>Manages Async Operations</strong>: Provides CompletableFuture-based async execution</li>
 * </ul>
 * <h3>Implementation Notes:</h3>
 * <p>
 * Concrete implementations handle framework-specific details:
 * </p>
 * <ul>
 *   <li><strong>JavaFX</strong>: Uses WebEngine.executeScript() for JavaScript execution</li>
 *   <li><strong>Vaadin</strong>: Uses Element.executeJs() for client-side JavaScript calls</li>
 * </ul>
 * <h3>Usage Example:</h3>
 * <pre>{@code
 * // Void operation (no return value)
 * JLTransportRequest voidRequest = JLTransportRequest.voidCall(circle, "setRadius", 1000);
 * transporter.execute(voidRequest); // CompletableFuture<Void>
 *
 * // Returnable operation (with result)
 * JLTransportRequest returnRequest = JLTransportRequest.returnableCall(circle, "getBounds", JLBounds.class);
 * CompletableFuture<JLBounds> bounds = transporter.execute(returnRequest);
 * bounds.thenAccept(b -> System.out.println("Bounds: " + b));
 * }</pre>
 * <p>
 * <strong>Thread Safety:</strong> Implementations must ensure thread-safe execution,
 * typically by marshaling calls to the appropriate UI thread.
 * </p>
 *
 * @param <T> The framework-specific result type (e.g., Object for JavaFX, JsonValue for Vaadin)
 * @author Matt Akbarian  (@makbn)
 * @since 1.0.0
 */
@FunctionalInterface
public interface JLServerToClientTransporter<T> {

    /**
     * Provides the core transport function for executing JavaScript operations.
     * <p>
     * This method returns a function that takes a {@link JLTransportRequest} and
     * executes the corresponding JavaScript operation, returning the raw result
     * in the framework-specific format.
     * </p>
     * <p>
     * <strong>Implementation Responsibility:</strong> Concrete implementations must:
     * </p>
     * <ul>
     *   <li>Generate proper JavaScript code from the transport request</li>
     *   <li>Execute the JavaScript in the web view/browser context</li>
     *   <li>Return the raw result for further processing</li>
     *   <li>Handle execution errors appropriately</li>
     * </ul>
     *
     * @return a function that executes JavaScript operations and returns raw results
     */
    Function<JLTransportRequest, T> serverToClientTransport();

    /**
     * Converts raw JavaScript execution results to typed Java objects.
     * <p>
     * This method handles the conversion from framework-specific raw results
     * (e.g., JavaScript objects, JSON values) to strongly-typed Java objects.
     * The default implementation throws {@link UnsupportedOperationException}
     * and must be overridden by concrete implementations.
     * </p>
     * <p>
     * <strong>Implementation Notes:</strong> Concrete implementations should:
     * </p>
     * <ul>
     *   <li>Parse the raw result based on the target class type</li>
     *   <li>Handle primitive types, collections, and custom objects</li>
     *   <li>Provide appropriate error handling for conversion failures</li>
     *   <li>Return a completed or failed CompletableFuture</li>
     * </ul>
     * <h4>Example Implementation Pattern:</h4>
     * <pre>{@code
     * public <M> CompletableFuture<M> covertResult(Object result, Class<M> clazz) {
     *     try {
     *         if (clazz == String.class) {
     *             return CompletableFuture.completedFuture(clazz.cast(result.toString()));
     *         } else if (clazz == JLBounds.class) {
     *             // Parse bounds from JSON string
     *             return CompletableFuture.completedFuture(clazz.cast(parseBounds(result)));
     *         }
     *         // Handle other types...
     *     } catch (Exception e) {
     *         return CompletableFuture.failedFuture(e);
     *     }
     * }
     * }</pre>
     *
     * @param <M>    the target Java type for conversion
     * @param result the raw result from JavaScript execution
     * @param clazz  the target class for type conversion
     * @return a CompletableFuture containing the converted result
     * @throws JLException if conversion fails or is not supported
     */
    default <M> CompletableFuture<M> covertResult(T result, Class<M> clazz) {
        try {
            throw new UnsupportedOperationException("Not implemented");
        } catch (Exception e) {
            throw new JLException("Error converting transport result", e);
        }
    }

    /**
     * Executes a transport request and returns a CompletableFuture with the result.
     * <p>
     * This is the main execution method that coordinates the JavaScript operation
     * and result conversion. It handles both void operations (no return value)
     * and returnable operations (with typed results).
     * </p>
     * <h3>Execution Flow:</h3>
     * <ol>
     *   <li><strong>Void Operations</strong>: Execute JavaScript without expecting return value</li>
     *   <li><strong>Returnable Operations</strong>: Execute JavaScript and convert result to target type</li>
     *   <li><strong>Error Handling</strong>: Wrap execution errors in {@link JLException}</li>
     * </ol>
     * <h4>Usage Examples:</h4>
     * <pre>{@code
     * // Void operation (e.g., setting properties)
     * JLTransportRequest setRadius = JLTransportRequest.voidCall(circle, "setRadius", 1000);
     * CompletableFuture<Void> voidResult = transporter.execute(setRadius);
     *
     * // Returnable operation (e.g., getting values)
     * JLTransportRequest getBounds = JLTransportRequest.returnableCall(circle, "getBounds", JLBounds.class);
     * CompletableFuture<JLBounds> boundsResult = transporter.execute(getBounds);
     * boundsResult.thenAccept(bounds -> {
     *     System.out.println("Circle bounds: " + bounds);
     * });
     * }</pre>
     * <p>
     * <strong>Thread Safety:</strong> This method must be safe to call from any thread,
     * with implementations ensuring JavaScript execution occurs on the appropriate UI thread.
     * </p>
     *
     * @param <M>       the expected return type for returnable operations
     * @param transport the transport request containing operation details
     * @return a CompletableFuture that will complete with the operation result
     * @throws JLException if the transport operation fails or no transporter is available
     */
    default <M> CompletableFuture<M> execute(JLTransportRequest transport) {
        if (transport.clazz() != Void.class) {
            // Returnable operation - execute and convert result
            T raw = serverToClientTransport().apply(transport);
            if (raw == null) {
                throw new JLException("No client to server transport found");
            }
            return covertResult(raw, transport.getCastedClazz());
        } else {
            // Void operation - execute without result conversion
            serverToClientTransport().apply(transport);
            return CompletableFuture.completedFuture(null);
        }
    }

}
