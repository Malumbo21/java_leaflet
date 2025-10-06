package io.github.makbn.jlmap.fx.test.engine;

import io.github.makbn.jlmap.fx.engine.JLJavaFXEngine;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test for JLJavaFXEngine class.
 * Note: These tests focus on testing the constructor and basic functionality without JavaFX mocking.
 */
class JLJavaFXEngineTest {

    @Test
    void constructor_withNullWebEngine_shouldAcceptNullEngine() {
        // When/Then - Constructor validation is not implemented in the actual class
        // This test documents the current behavior
        JLJavaFXEngine engine = new JLJavaFXEngine(null);
        assertThat(engine).isNotNull();
    }

    @Test
    void jlJavaFXEngine_shouldExtendJLWebEngine() {
        // This test verifies the inheritance hierarchy without requiring JavaFX initialization
        Class<?> engineClass = JLJavaFXEngine.class;

        // Then
        assertThat(engineClass.getSuperclass().getSimpleName()).isEqualTo("JLWebEngine");
    }

    @Test
    void jlJavaFXEngine_shouldHaveCorrectConstructorParameter() {
        // This test verifies the constructor exists with correct parameter types
        boolean hasCorrectConstructor = false;

        try {
            JLJavaFXEngine.class.getConstructor(javafx.scene.web.WebEngine.class);
            hasCorrectConstructor = true;
        } catch (NoSuchMethodException e) {
            // Constructor not found
        }

        // Then
        assertThat(hasCorrectConstructor).isTrue();
    }

    @Test
    void jlJavaFXEngine_shouldImplementRequiredMethods() {
        // This test verifies required methods exist without requiring JavaFX initialization
        Class<?> engineClass = JLJavaFXEngine.class;

        boolean hasExecuteScriptMethod = false;
        boolean hasGetStatusMethod = false;

        try {
            engineClass.getDeclaredMethod("executeScript", String.class, Class.class);
            hasExecuteScriptMethod = true;
        } catch (NoSuchMethodException e) {
            // Method not found
        }

        try {
            engineClass.getDeclaredMethod("getStatus");
            hasGetStatusMethod = true;
        } catch (NoSuchMethodException e) {
            // Method not found
        }

        // Then
        assertThat(hasExecuteScriptMethod).isTrue();
        assertThat(hasGetStatusMethod).isTrue();
    }
}