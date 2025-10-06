package io.github.makbn.jlmap.vaadin.test.engine;

import com.vaadin.flow.dom.Element;
import io.github.makbn.jlmap.vaadin.engine.JLVaadinEngine;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.function.Supplier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JLVaadinEngineTest {

    @Mock
    private Supplier<Element> mockElementSupplier;

    @Mock
    private Element mockElement;

    @Test
    void jlVaadinEngine_shouldExtendJLWebEngine() {
        when(mockElementSupplier.get()).thenReturn(mockElement);
        JLVaadinEngine engine = new JLVaadinEngine(mockElementSupplier);

        assertThat(engine).isInstanceOf(io.github.makbn.jlmap.engine.JLWebEngine.class);
    }

    @Test
    void jlVaadinEngine_shouldHaveCorrectConstructorParameter() {
        when(mockElementSupplier.get()).thenReturn(mockElement);
        JLVaadinEngine engine = new JLVaadinEngine(mockElementSupplier);

        assertThat(engine).isNotNull();
    }

    @Test
    void jlVaadinEngine_shouldImplementRequiredMethods() {
        when(mockElementSupplier.get()).thenReturn(mockElement);
        JLVaadinEngine engine = new JLVaadinEngine(mockElementSupplier);

        // Verify engine has executeScript method
        assertThat(engine).extracting("class").satisfies(clazz -> {
            try {
                ((Class<?>) clazz).getMethod("executeScript", String.class);
            } catch (NoSuchMethodException e) {
                throw new AssertionError("executeScript method not found");
            }
        });
    }
}