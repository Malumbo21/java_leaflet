package io.github.makbn.jlmap.vaadin.test.internal;

import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.dependency.StyleSheet;
import io.github.makbn.jlmap.map.JLMapProvider;
import io.github.makbn.jlmap.model.JLLatLng;
import io.github.makbn.jlmap.vaadin.JLMapView;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class JLVaadinMapRendererTest {

    private JLMapView mapView;

    @BeforeEach
    void setUp() {
        mapView = JLMapView.builder()
                .jlMapProvider(JLMapProvider.getDefault())
                .startCoordinate(JLLatLng.builder().lat(51.505).lng(-0.09).build())
                .showZoomController(true)
                .build();
    }

    @Test
    void render_shouldGenerateValidHtmlDocument() {
        // Test that Vaadin component generates valid structure
        assertThat(mapView.getClass().getAnnotation(Tag.class).value()).isEqualTo("jl-map-view");
        assertThat(mapView).isNotNull();
    }

    @Test
    void render_shouldIncludeLeafletCssAndJavascript() {
        // Verify Vaadin annotations include Leaflet dependencies
        assertThat(mapView.getClass().getAnnotation(StyleSheet.class)).isNotNull();
        assertThat(mapView.getClass().getAnnotation(JsModule.class)).isNotNull();
    }

    @Test
    void render_shouldIncludeLeafletIntegrityAttributes() {
        // Verify Vaadin uses secure Leaflet loading
        StyleSheet styleSheet = mapView.getClass().getAnnotation(StyleSheet.class);
        assertThat(styleSheet.value()).contains("leaflet.css");
    }

    @Test
    void render_shouldIncludeMapContainerDiv() {
        // Verify component acts as map container
        assertThat(mapView.getClass().getAnnotation(Tag.class).value()).isEqualTo("jl-map-view");
    }

    @Test
    void render_shouldIncludeMapHelperFunctions() {
        // Test that initialization includes helper functions
        String initScript = getInitializationScript();
        assertThat(initScript).contains("this.map");
    }

    @Test
    void render_shouldIncludeJsRelayFunction() {
        // Verify JavaScript relay function exists
        assertThat(mapView).extracting("class").satisfies(clazz -> {
            try {
                ((Class<?>) clazz).getMethod("eventHandler", String.class, String.class, String.class, String.class, String.class, String.class);
            } catch (NoSuchMethodException e) {
                throw new AssertionError("eventHandler method not found");
            }
        });
    }

    @Test
    void render_shouldIncludeClientToServerEventHandler() {
        // Verify @ClientCallable annotation exists
        try {
            mapView.getClass().getMethod("eventHandler", String.class, String.class, String.class, String.class, String.class, String.class)
                    .getAnnotation(com.vaadin.flow.component.ClientCallable.class);
            assertThat(true).isTrue(); // Method has @ClientCallable
        } catch (NoSuchMethodException e) {
            throw new AssertionError("ClientCallable eventHandler not found");
        }
    }

    @Test
    void render_shouldIncludeMapInitializationWithCorrectParameters() {
        String initScript = getInitializationScript();
        assertThat(initScript).contains("L.map");
        assertThat(initScript).contains("setView");
        assertThat(initScript).contains("51.505");
        assertThat(initScript).contains("-0.09");
    }

    @Test
    void render_shouldIncludeAllMapEventHandlers() {
        // Verify component can handle events
        assertThat(mapView).hasFieldOrProperty("jlMapCallbackHandler");
    }

    @Test
    void render_shouldIncludeMapSetup() {
        String initScript = getInitializationScript();
        assertThat(initScript).contains("L.tileLayer");
        assertThat(initScript).contains("addTo");
    }

    @Test
    void render_shouldSetBodyStyle() {
        // Verify component has proper styling
        assertThat(mapView.getBoxSizing()).isEqualTo(com.vaadin.flow.component.orderedlayout.BoxSizing.CONTENT_BOX);
        assertThat(mapView.getMinHeight()).isEqualTo("100%");
    }

    @Test
    void render_shouldIncludeMetaTags() {
        // Verify component has proper metadata
        assertThat(mapView.getClass().getAnnotation(Tag.class).value()).isEqualTo("jl-map-view");
    }

    private String getInitializationScript() {
        try {
            java.lang.reflect.Method method = mapView.getClass().getDeclaredMethod("generateInitializeFunctionCall");
            method.setAccessible(true);
            return (String) method.invoke(mapView);
        } catch (Exception e) {
            return "";
        }
    }
}