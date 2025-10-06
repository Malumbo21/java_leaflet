package io.github.makbn.jlmap.vaadin.engine;

import com.google.gson.Gson;
import com.vaadin.flow.component.page.PendingJavaScriptResult;
import com.vaadin.flow.internal.JsonCodec;
import elemental.json.JsonType;
import io.github.makbn.jlmap.engine.JLServerToClientTransporter;
import io.github.makbn.jlmap.exception.JLConversionException;
import lombok.SneakyThrows;

import java.util.Set;
import java.util.concurrent.CompletableFuture;

/**
 * Vaadin-specific implementation of the server-to-client transport interface.
 * <p>
 * This implementation uses Vaadin's {@link PendingJavaScriptResult} to execute JavaScript
 * operations and convert results back to Java objects. It leverages Vaadin's JSON handling
 * infrastructure and provides automatic type conversion for common data types.
 * </p>
 * <h3>Implementation Details:</h3>
 * <ul>
 *   <li><strong>Execution Context</strong>: Uses Vaadin Element.executeJs() for client-side execution</li>
 *   <li><strong>Result Handling</strong>: Converts PendingJavaScriptResult to typed Java objects</li>
 *   <li><strong>Type Support</strong>: Handles primitives, JSON objects, and custom classes via Gson</li>
 *   <li><strong>Async Operations</strong>: All operations return CompletableFuture for non-blocking execution</li>
 * </ul>
 * <h3>Supported Type Conversions:</h3>
 * <ul>
 *   <li><strong>Basic Types</strong>: String, Boolean, Double, Integer (via JsonCodec)</li>
 *   <li><strong>JSON Objects</strong>: Complex objects converted via Gson</li>
 *   <li><strong>String Serialization</strong>: JSON objects can be returned as JSON strings</li>
 * </ul>
 * <p>
 * <strong>Thread Safety:</strong> Operations are automatically marshaled to the Vaadin UI thread
 * through the PendingJavaScriptResult mechanism.
 * </p>
 *
 * @author Matt Akbarian (@makbn)
 * @since 2.0.0
 */
public abstract class JLVaadinServerToClientTransporter implements JLServerToClientTransporter<PendingJavaScriptResult> {

    /**
     * Set of basic types that can be converted directly via Vaadin's JsonCodec
     */
    private static final Set<Class<?>> BASIC_TYPES = Set.of(String.class, Boolean.class, Double.class, Integer.class);

    /**
     * Gson instance for converting complex JSON objects to Java classes
     */
    Gson gson = new Gson();

    /**
     * Converts Vaadin's PendingJavaScriptResult to typed Java objects.
     * <p>
     * This implementation handles three categories of type conversion:
     * </p>
     * <ol>
     *   <li><strong>JSON Objects to String</strong>: Returns the raw JSON representation</li>
     *   <li><strong>Basic Types</strong>: Uses Vaadin's JsonCodec for direct conversion</li>
     *   <li><strong>Complex Objects</strong>: Uses Gson for JSON-to-object conversion</li>
     * </ol>
     * <p>
     * The conversion process is asynchronous, with the CompletableFuture completing
     * when the JavaScript execution finishes and the result is converted.
     * </p>
     *
     * @inheritDoc
     */
    @Override
    @SneakyThrows
    public <M> CompletableFuture<M> covertResult(PendingJavaScriptResult result, Class<M> clazz) {
        CompletableFuture<M> future = new CompletableFuture<>();

        // Set up async callback when JavaScript execution completes
        result.then(value -> {
            try {
                // Handle JSON objects requested as String - return raw JSON
                if (value.getType() == JsonType.OBJECT && clazz == String.class) {
                    //noinspection unchecked
                    future.complete((M) value.toJson());
                }
                // Handle basic types via Vaadin's JsonCodec
                else if (BASIC_TYPES.contains(clazz)) {
                    M convertedValue = JsonCodec.decodeAs(value, clazz);
                    future.complete(convertedValue);
                }
                // Handle complex objects via Gson JSON parsing
                else {
                    future.complete(gson.fromJson(value.toJson(), clazz));
                }
            } catch (ClassCastException e) {
                // Propagate conversion errors as JLConversionException
                future.completeExceptionally(new JLConversionException(e, value.toNative()));
            }
        });
        return future;
    }


}
