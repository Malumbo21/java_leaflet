package io.github.makbn.jlmap.fx.test.layer;

import io.github.makbn.jlmap.JLMapEventHandler;
import io.github.makbn.jlmap.engine.JLWebEngine;
import io.github.makbn.jlmap.fx.layer.JLVectorLayer;
import io.github.makbn.jlmap.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JLVectorLayerTest {

    @Mock
    private JLWebEngine<Object> engine;

    @Mock
    private JLMapEventHandler callbackHandler;

    private JLVectorLayer vectorLayer;

    @BeforeEach
    void setUp() {
        vectorLayer = new JLVectorLayer(engine, callbackHandler);
    }

    // === Constructor and Basic Tests ===

    @Test
    void constructor_withNullEngine_shouldAcceptNullEngine() {
        // When/Then - Constructor validation is not implemented in the actual class
        // This test documents the current behavior
        JLVectorLayer layer = new JLVectorLayer(null, callbackHandler);
        assertThat(layer).isNotNull();
    }

    @Test
    void constructor_withNullCallbackHandler_shouldAcceptNullHandler() {
        // When/Then - Constructor validation is not implemented in the actual class
        // This test documents the current behavior
        JLVectorLayer layer = new JLVectorLayer(engine, null);
        assertThat(layer).isNotNull();
    }

    @Test
    void vectorLayer_shouldExtendJLLayer() {
        // This test verifies the inheritance hierarchy
        Class<?> layerClass = JLVectorLayer.class;

        // Then
        assertThat(layerClass.getSuperclass().getSimpleName()).isEqualTo("JLLayer");
    }

    // === Polyline Tests ===

    @Test
    void addPolyline_withValidVertices_shouldCreatePolylineAndExecuteScript() {
        // Given
        JLLatLng[] vertices = {
                JLLatLng.builder().lat(52.5200).lng(13.4050).build(),
                JLLatLng.builder().lat(52.5300).lng(13.4150).build()
        };

        // When
        JLPolyline result = vectorLayer.addPolyline(vertices);

        // Then
        ArgumentCaptor<String> scriptCaptor = ArgumentCaptor.forClass(String.class);
        verify(engine).executeScript(scriptCaptor.capture());
        verify(callbackHandler).addJLObject(anyString(), eq(result));

        String script = scriptCaptor.getValue();
        assertThat(script).contains("L.polyline");
        assertThat(script).contains("[52.52,13.405]");
        assertThat(script).contains("[52.53,13.415]");
        assertThat(script).contains("addTo(this.map)");

        assertThat(result).isNotNull();
        assertThat(result.getJLId()).startsWith("JLGeoJson");
    }

    @Test
    void addPolyline_withCustomOptions_shouldIncludeOptionsInScript() {
        // Given
        JLLatLng[] vertices = {
                JLLatLng.builder().lat(52.5200).lng(13.4050).build(),
                JLLatLng.builder().lat(52.5300).lng(13.4150).build()
        };
        JLOptions options = JLOptions.builder()
                .color(JLColor.RED)
                .weight(5)
                .opacity(0.8)
                .build();

        // When
        JLPolyline result = vectorLayer.addPolyline(vertices, options);

        // Then
        ArgumentCaptor<String> scriptCaptor = ArgumentCaptor.forClass(String.class);
        verify(engine).executeScript(scriptCaptor.capture());
        verify(callbackHandler).addJLObject(anyString(), eq(result));

        String script = scriptCaptor.getValue();
        assertThat(script).contains("weight: 5");
        assertThat(script).contains("opacity: 0.8");

        assertThat(result).isNotNull();
    }

    @Test
    void addPolyline_withNullVertices_shouldThrowNullPointerException() {
        // When/Then
        assertThatThrownBy(() -> vectorLayer.addPolyline(null))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    void addPolyline_withEmptyVertices_shouldAcceptEmptyArray() {
        // Given
        JLLatLng[] emptyVertices = new JLLatLng[0];

        // When/Then - Empty vertices validation is not implemented in the actual class
        // This test documents the current behavior
        JLPolyline polyline = vectorLayer.addPolyline(emptyVertices);
        assertThat(polyline).isNotNull();
    }

    @Test
    void removePolyline_shouldExecuteRemoveScript() {
        // Given
        String polylineId = "testPolylineId";

        // When
        boolean result = vectorLayer.removePolyline(polylineId);

        // Then
        verify(engine).executeScript("this.map.removeLayer(this.testPolylineId)");
        verify(callbackHandler).remove(JLPolyline.class, polylineId);
        verify(callbackHandler).remove(JLMultiPolyline.class, polylineId);
        assertThat(result).isTrue();
    }

    // === Circle Tests ===

    @Test
    void addCircle_withDefaultOptions_shouldCreateCircleWithDefaults() {
        // Given
        JLLatLng center = JLLatLng.builder().lat(52.5200).lng(13.4050).build();

        // When
        JLCircle result = vectorLayer.addCircle(center);

        // Then
        ArgumentCaptor<String> scriptCaptor = ArgumentCaptor.forClass(String.class);
        verify(engine).executeScript(scriptCaptor.capture());
        verify(callbackHandler).addJLObject(anyString(), eq(result));

        String script = scriptCaptor.getValue();
        assertThat(script).contains("L.circle");
        assertThat(script).contains("[52.520000, 13.405000]");
        assertThat(script).contains("radius: 1000.000000"); // Default radius
        assertThat(script).contains("on('move'");
        assertThat(script).contains("on('add'");
        assertThat(script).contains("on('remove'");

        assertThat(result).isNotNull();
        assertThat(result.getJLId()).startsWith("JLCircle");
    }

    @Test
    void addCircle_withCustomRadius_shouldUseCustomRadius() {
        // Given
        JLLatLng center = JLLatLng.builder().lat(52.5200).lng(13.4050).build();
        int customRadius = 500;

        // When
        JLCircle result = vectorLayer.addCircle(center, customRadius, JLOptions.DEFAULT);

        // Then
        ArgumentCaptor<String> scriptCaptor = ArgumentCaptor.forClass(String.class);
        verify(engine).executeScript(scriptCaptor.capture());

        String script = scriptCaptor.getValue();
        assertThat(script).contains("radius: 500.000000");

        assertThat(result).isNotNull();
    }

    @Test
    void addCircle_withNullCenter_shouldThrowNullPointerException() {
        // When/Then
        assertThatThrownBy(() -> vectorLayer.addCircle(null))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    void removeCircle_shouldExecuteRemoveScript() {
        // Given
        String circleId = "testCircleId";
        when(engine.executeScript(anyString())).thenReturn("true");

        // When
        boolean result = vectorLayer.removeCircle(circleId);

        // Then
        verify(engine).executeScript("this.map.removeLayer(this.testCircleId)");
        verify(callbackHandler).remove(JLCircle.class, circleId);
        assertThat(result).isTrue();
    }

    // === Circle Marker Tests ===

    @Test
    void addCircleMarker_withDefaultOptions_shouldCreateCircleMarker() {
        // Given
        JLLatLng center = JLLatLng.builder().lat(52.5200).lng(13.4050).build();

        // When
        JLCircleMarker result = vectorLayer.addCircleMarker(center);

        // Then
        ArgumentCaptor<String> scriptCaptor = ArgumentCaptor.forClass(String.class);
        verify(engine).executeScript(scriptCaptor.capture());
        verify(callbackHandler).addJLObject(anyString(), eq(result));

        String script = scriptCaptor.getValue();
        assertThat(script).contains("L.circleMarker");
        assertThat(script).contains("[52.520000, 13.405000]");
        assertThat(script).contains("on('move'");
        assertThat(script).contains("on('click'");
        assertThat(script).contains("on('dblclick'");

        assertThat(result).isNotNull();
        assertThat(result.getJLId()).startsWith("JLCircleMarker");
    }

    @Test
    void addCircleMarker_withNullCenter_shouldThrowNullPointerException() {
        // When/Then
        assertThatThrownBy(() -> vectorLayer.addCircleMarker(null))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    void removeCircleMarker_shouldExecuteRemoveScript() {
        // Given
        String circleMarkerId = "testCircleMarkerId";
        when(engine.executeScript(anyString())).thenReturn("true");

        // When
        boolean result = vectorLayer.removeCircleMarker(circleMarkerId);

        // Then
        verify(engine).executeScript("this.map.removeLayer(this.testCircleMarkerId)");
        verify(callbackHandler).remove(JLCircleMarker.class, circleMarkerId);
        assertThat(result).isTrue();
    }

    // === Polygon Tests ===

    @Test
    void addPolygon_withValidVertices_shouldCreatePolygon() {
        // Given
        JLLatLng[][][] vertices = {
                {
                        {
                                JLLatLng.builder().lat(52.5200).lng(13.4050).build(),
                                JLLatLng.builder().lat(52.5300).lng(13.4150).build(),
                                JLLatLng.builder().lat(52.5400).lng(13.4250).build()
                        }
                }
        };

        // When
        JLPolygon result = vectorLayer.addPolygon(vertices);

        // Then
        ArgumentCaptor<String> scriptCaptor = ArgumentCaptor.forClass(String.class);
        verify(engine).executeScript(scriptCaptor.capture());
        verify(callbackHandler).addJLObject(anyString(), eq(result));

        String script = scriptCaptor.getValue();
        assertThat(script).contains("L.polygon");
        assertThat(script).contains("[52.520000, 13.405000]");
        assertThat(script).contains("[52.530000, 13.415000]");
        assertThat(script).contains("[52.540000, 13.425000]");

        assertThat(result).isNotNull();
        assertThat(result.getJLId()).startsWith("JLGeoJson");
    }

    @Test
    void addPolygon_withNullVertices_shouldThrowNullPointerException() {
        // When/Then
        assertThatThrownBy(() -> vectorLayer.addPolygon(null))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    void removePolygon_shouldExecuteRemoveScript() {
        // Given
        String polygonId = "testPolygonId";
        when(engine.executeScript(anyString())).thenReturn("true");

        // When
        boolean result = vectorLayer.removePolygon(polygonId);

        // Then
        verify(engine).executeScript("this.map.removeLayer(this.testPolygonId)");
        verify(callbackHandler).remove(JLPolygon.class, polygonId);
        assertThat(result).isTrue();
    }

    // === Multi-Polyline Tests ===

    @Test
    void addMultiPolyline_withValidVertices_shouldCreateMultiPolyline() {
        // Given
        JLLatLng[][] vertices = {
                {
                        JLLatLng.builder().lat(52.5200).lng(13.4050).build(),
                        JLLatLng.builder().lat(52.5300).lng(13.4150).build()
                },
                {
                        JLLatLng.builder().lat(52.5400).lng(13.4250).build(),
                        JLLatLng.builder().lat(52.5500).lng(13.4350).build()
                }
        };

        // When
        JLMultiPolyline result = vectorLayer.addMultiPolyline(vertices);

        // Then
        ArgumentCaptor<String> scriptCaptor = ArgumentCaptor.forClass(String.class);
        verify(engine).executeScript(scriptCaptor.capture());
        verify(callbackHandler).addJLObject(anyString(), eq(result));

        String script = scriptCaptor.getValue();
        assertThat(script).contains("L.polyline");
        assertThat(script).contains("[52.520000,13.405000]");
        assertThat(script).contains("[52.540000,13.425000]");

        assertThat(result).isNotNull();
        assertThat(result.getJLId()).startsWith("JLGeoJson");
    }

    @Test
    void addMultiPolyline_withNullVertices_shouldThrowNullPointerException() {
        // When/Then
        assertThatThrownBy(() -> vectorLayer.addMultiPolyline(null))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    void removeMultiPolyline_shouldExecuteRemoveScript() {
        // Given
        String multiPolylineId = "testMultiPolylineId";

        // When
        boolean result = vectorLayer.removeMultiPolyline(multiPolylineId);

        // Then
        verify(engine).executeScript("this.map.removeLayer(this.testMultiPolylineId)");
        verify(callbackHandler).remove(JLMultiPolyline.class, multiPolylineId);
        assertThat(result).isTrue();
    }

    // === ID Generation Tests ===

    @Test
    void vectorLayer_shouldGenerateUniqueIds() {
        // Given
        JLLatLng center = JLLatLng.builder().lat(52.5200).lng(13.4050).build();

        // When
        JLCircle circle1 = vectorLayer.addCircle(center);
        JLCircle circle2 = vectorLayer.addCircle(center);

        // Then
        assertThat(circle1.getJLId()).isNotEqualTo(circle2.getJLId());
        assertThat(circle1.getJLId()).startsWith("JLCircle");
        assertThat(circle2.getJLId()).startsWith("JLCircle");
    }
}