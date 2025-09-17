package io.github.makbn.jlmap.fx.engine;

import com.google.gson.reflect.TypeToken;
import io.github.makbn.jlmap.engine.JLServerToClientTransporter;

import java.lang.reflect.Type;
import java.util.concurrent.CompletableFuture;

/**
 * JavaFX-specific implementation of the server-to-client transport interface.
 * <p>
 * This implementation uses JavaFX's WebEngine.executeScript() to execute JavaScript
 * operations and provides basic type conversion for common result types. It leverages
 * JavaFX's synchronous JavaScript execution model with CompletableFuture wrapping for
 * consistent async API.
 * </p>
 * <h3>Implementation Details:</h3>
 * <ul>
 *   <li><strong>Execution Context</strong>: Uses WebEngine.executeScript() for synchronous JavaScript execution</li>
 *   <li><strong>Result Handling</strong>: Converts JavaScript objects to Java primitives and strings</li>
 *   <li><strong>Type Support</strong>: Basic string conversion with fallback to parent implementation</li>
 *   <li><strong>Thread Model</strong>: Must be called from JavaFX Application Thread</li>
 * </ul>
 * <h3>Supported Type Conversions:</h3>
 * <ul>
 *   <li><strong>String</strong>: Direct conversion via String.valueOf()</li>
 *   <li><strong>Null Values</strong>: Handled gracefully with null CompletableFuture</li>
 *   <li><strong>Other Types</strong>: Delegated to parent implementation</li>
 * </ul>
 * <p>
 * <strong>Thread Safety:</strong> Must be executed on the JavaFX Application Thread.
 * Concrete implementations should ensure proper thread marshaling.
 * </p>
 *
 * @author Matt Akbarian (@makbn)
 * @since 2.0.0
 */
public abstract class JLJavaFxServerToClientTransporter implements JLServerToClientTransporter<Object> {

    /**
     * Converts JavaFX WebEngine execution results to typed Java objects.
     * <p>
     * This implementation provides basic type conversion with special handling for:
     * </p>
     * <ul>
     *   <li><strong>Null Results</strong>: Returns completed future with null value</li>
     *   <li><strong>String Types</strong>: Converts any result to string representation</li>
     *   <li><strong>Other Types</strong>: Delegates to parent implementation</li>
     * </ul>
     * <p>
     * <strong>Note:</strong> JavaFX's WebEngine.executeScript() returns raw JavaScript
     * objects which may need additional conversion for complex types.
     * </p>
     *
     * @inheritDoc
     */
    @Override
    public <M> CompletableFuture<M> covertResult(Object result, Class<M> resultType) {
        // Handle null results gracefully
        if (result == null) {
            return CompletableFuture.completedFuture(null);
        }

        // Special handling for String type - convert any result to string
        Type type = new TypeToken<CompletableFuture<M>>() {
        }.getType();
        if (type.getTypeName().equals("String")) {
            return CompletableFuture.completedFuture((M) String.valueOf(result));
        }

        // Delegate complex type conversion to parent implementation
        return JLServerToClientTransporter.super.covertResult(result, resultType);
    }
}
