package io.github.makbn.jlmap.vaadin.test.layer;

import com.vaadin.flow.component.page.PendingJavaScriptResult;
import io.github.makbn.jlmap.JLMapCallbackHandler;
import io.github.makbn.jlmap.JLProperties;
import io.github.makbn.jlmap.engine.JLWebEngine;
import io.github.makbn.jlmap.model.*;
import io.github.makbn.jlmap.vaadin.layer.JLVaadinVectorLayer;
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
class JLVaadinVectorLayerTest {

    @Mock
    private JLWebEngine<PendingJavaScriptResult> engine;

    @Mock
    private JLMapCallbackHandler callbackHandler;

    @Mock
    private PendingJavaScriptResult mockJavaScriptResult;

    private JLVaadinVectorLayer vectorLayer;

    @BeforeEach
    void setUp() {
        vectorLayer = new JLVaadinVectorLayer(engine, callbackHandler);
    }

    // === Constructor and Basic Tests ===

    @Test
    void constructor_withNullEngine_shouldAcceptNullEngine() {
        JLVaadinVectorLayer layer = new JLVaadinVectorLayer(null, callbackHandler);
        assertThat(layer).isNotNull();
    }

    @Test
    void constructor_withNullCallbackHandler_shouldAcceptNullHandler() {
        JLVaadinVectorLayer layer = new JLVaadinVectorLayer(engine, null);
        assertThat(layer).isNotNull();
    }

    @Test
    void vectorLayer_shouldExtendJLLayer() {
        assertThat(vectorLayer).isInstanceOf(io.github.makbn.jlmap.vaadin.layer.JLVaadinLayer.class);
    }

    // === Polyline Tests ===

    @Test
    void addPolyline_withValidVertices_shouldCreatePolylineAndExecuteScript() {
        when(engine.executeScript(anyString())).thenReturn(mockJavaScriptResult);
        JLLatLng[] vertices = {
                JLLatLng.builder().lat(52.5200).lng(13.4050).build(),
                JLLatLng.builder().lat(52.5300).lng(13.4150).build()
        };

        JLPolyline result = vectorLayer.addPolyline(vertices);

        ArgumentCaptor<String> scriptCaptor = ArgumentCaptor.forClass(String.class);
        verify(engine).executeScript(scriptCaptor.capture());
        verify(callbackHandler).addJLObject(anyString(), eq(result));

        String script = scriptCaptor.getValue();
        assertThat(script).contains("L.polyline");
        assertThat(script).contains("[52.52,13.405]");
        assertThat(script).contains("[52.53,13.415]");
        assertThat(script).contains("addTo(this.map)");

        assertThat(result).isNotNull();
        assertThat(result.getId()).startsWith(JLPolyline.class.getSimpleName());
    }

    @Test
    void addPolyline_withCustomOptions_shouldIncludeOptionsInScript() {
        when(engine.executeScript(anyString())).thenReturn(mockJavaScriptResult);
        JLLatLng[] vertices = {
                JLLatLng.builder().lat(52.5200).lng(13.4050).build(),
                JLLatLng.builder().lat(52.5300).lng(13.4150).build()
        };
        JLOptions options = JLOptions.builder()
                .color(JLColor.RED)
                .weight(5)
                .opacity(0.8)
                .build();

        JLPolyline result = vectorLayer.addPolyline(vertices, options);

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
        assertThatThrownBy(() -> vectorLayer.addPolyline(null))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    void addPolyline_withEmptyVertices_shouldAcceptEmptyArray() {
        when(engine.executeScript(anyString())).thenReturn(mockJavaScriptResult);
        JLLatLng[] emptyVertices = new JLLatLng[0];

        JLPolyline polyline = vectorLayer.addPolyline(emptyVertices);
        assertThat(polyline).isNotNull();
    }

    @Test
    void removePolyline_shouldExecuteRemoveScript() {
        when(engine.executeScript(anyString())).thenReturn(mockJavaScriptResult);
        String polylineId = "testPolylineId";

        boolean result = vectorLayer.removePolyline(polylineId);

        verify(engine).executeScript("this.map.removeLayer(this.testPolylineId)");
        verify(callbackHandler).remove(JLPolyline.class, polylineId);
        assertThat(result).isTrue();
    }

    // === Circle Tests ===

    @Test
    void addCircle_withDefaultOptions_shouldCreateCircleWithDefaults() {
        when(engine.executeScript(anyString())).thenReturn(mockJavaScriptResult);
        JLLatLng center = JLLatLng.builder().lat(52.5200).lng(13.4050).build();

        JLCircle result = vectorLayer.addCircle(center);

        ArgumentCaptor<String> scriptCaptor = ArgumentCaptor.forClass(String.class);
        verify(engine).executeScript(scriptCaptor.capture());
        verify(callbackHandler).addJLObject(anyString(), eq(result));

        String script = scriptCaptor.getValue();
        assertThat(script).contains("L.circle");
        assertThat(script).contains("[52.520000, 13.405000]");
        assertThat(script).contains("radius: " + JLProperties.DEFAULT_CIRCLE_RADIUS);

        assertThat(script).contains("on('add'");
        assertThat(script).contains("on('remove'");

        assertThat(result).isNotNull();
        assertThat(result.getId()).startsWith("JLCircle");
    }

    @Test
    void addCircle_withCustomRadius_shouldUseCustomRadius() {
        when(engine.executeScript(anyString())).thenReturn(mockJavaScriptResult);
        JLLatLng center = JLLatLng.builder().lat(52.5200).lng(13.4050).build();
        int customRadius = 500;

        JLCircle result = vectorLayer.addCircle(center, customRadius, JLOptions.DEFAULT);

        ArgumentCaptor<String> scriptCaptor = ArgumentCaptor.forClass(String.class);
        verify(engine).executeScript(scriptCaptor.capture());

        String script = scriptCaptor.getValue();
        assertThat(script).contains("radius: 500.000000");

        assertThat(result).isNotNull();
    }

    @Test
    void addCircle_withNullCenter_shouldThrowNullPointerException() {
        assertThatThrownBy(() -> vectorLayer.addCircle(null))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    void removeCircle_shouldExecuteRemoveScript() {
        when(engine.executeScript(anyString())).thenReturn(mockJavaScriptResult);
        String circleId = "testCircleId";

        boolean result = vectorLayer.removeCircle(circleId);

        verify(engine).executeScript("this.map.removeLayer(this.testCircleId)");
        verify(callbackHandler).remove(JLCircle.class, circleId);
        assertThat(result).isTrue();
    }

    // === Circle Marker Tests ===

    @Test
    void addCircleMarker_withDefaultOptions_shouldCreateCircleMarker() {
        when(engine.executeScript(anyString())).thenReturn(mockJavaScriptResult);
        JLLatLng center = JLLatLng.builder().lat(52.5200).lng(13.4050).build();

        JLCircleMarker result = vectorLayer.addCircleMarker(center);

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
        assertThat(result.getId()).startsWith("JLCircleMarker");
    }

    @Test
    void addCircleMarker_withNullCenter_shouldThrowNullPointerException() {
        assertThatThrownBy(() -> vectorLayer.addCircleMarker(null))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    void removeCircleMarker_shouldExecuteRemoveScript() {
        when(engine.executeScript(anyString())).thenReturn(mockJavaScriptResult);
        String circleMarkerId = "testCircleMarkerId";

        boolean result = vectorLayer.removeCircleMarker(circleMarkerId);

        verify(engine).executeScript("this.map.removeLayer(this.testCircleMarkerId)");
        verify(callbackHandler).remove(JLCircleMarker.class, circleMarkerId);
        assertThat(result).isTrue();
    }

    // === Polygon Tests ===

    @Test
    void addPolygon_withValidVertices_shouldCreatePolygon() {
        when(engine.executeScript(anyString())).thenReturn(mockJavaScriptResult);
        JLLatLng[][][] vertices = {
                {
                        {
                                JLLatLng.builder().lat(52.5200).lng(13.4050).build(),
                                JLLatLng.builder().lat(52.5300).lng(13.4150).build(),
                                JLLatLng.builder().lat(52.5400).lng(13.4250).build()
                        }
                }
        };

        JLPolygon result = vectorLayer.addPolygon(vertices);

        ArgumentCaptor<String> scriptCaptor = ArgumentCaptor.forClass(String.class);
        verify(engine).executeScript(scriptCaptor.capture());
        verify(callbackHandler).addJLObject(anyString(), eq(result));

        String script = scriptCaptor.getValue();
        assertThat(script).contains("L.polygon");
        assertThat(script).contains("[52.520000, 13.405000]");
        assertThat(script).contains("[52.530000, 13.415000]");
        assertThat(script).contains("[52.540000, 13.425000]");

        assertThat(result).isNotNull();
        assertThat(result.getId()).startsWith("JLPolygon_");
    }

    @Test
    void addPolygon_withNullVertices_shouldThrowNullPointerException() {
        assertThatThrownBy(() -> vectorLayer.addPolygon(null))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    void removePolygon_shouldExecuteRemoveScript() {
        when(engine.executeScript(anyString())).thenReturn(mockJavaScriptResult);
        String polygonId = "testPolygonId";

        boolean result = vectorLayer.removePolygon(polygonId);

        verify(engine).executeScript("this.map.removeLayer(this.testPolygonId)");
        verify(callbackHandler).remove(JLPolygon.class, polygonId);
        assertThat(result).isTrue();
    }

    // === Multi-Polyline Tests ===

    @Test
    void addMultiPolyline_withValidVertices_shouldCreateMultiPolyline() {
        when(engine.executeScript(anyString())).thenReturn(mockJavaScriptResult);
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

        JLMultiPolyline result = vectorLayer.addMultiPolyline(vertices);

        ArgumentCaptor<String> scriptCaptor = ArgumentCaptor.forClass(String.class);
        verify(engine).executeScript(scriptCaptor.capture());
        verify(callbackHandler).addJLObject(anyString(), eq(result));

        String script = scriptCaptor.getValue();
        assertThat(script).contains("L.polyline");
        assertThat(script).contains("[52.520000,13.405000]");
        assertThat(script).contains("[52.540000,13.425000]");

        assertThat(result).isNotNull();
        assertThat(result.getId()).startsWith("JLMultiPolyline_");
    }

    @Test
    void addMultiPolyline_withNullVertices_shouldThrowNullPointerException() {
        assertThatThrownBy(() -> vectorLayer.addMultiPolyline(null))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    void removeMultiPolyline_shouldExecuteRemoveScript() {
        when(engine.executeScript(anyString())).thenReturn(mockJavaScriptResult);
        String multiPolylineId = "testMultiPolylineId";

        boolean result = vectorLayer.removeMultiPolyline(multiPolylineId);

        verify(engine).executeScript("this.map.removeLayer(this.testMultiPolylineId)");
        verify(callbackHandler).remove(JLMultiPolyline.class, multiPolylineId);
        assertThat(result).isTrue();
    }

    // === ID Generation Tests ===

    @Test
    void vectorLayer_shouldGenerateUniqueIds() {
        when(engine.executeScript(anyString())).thenReturn(mockJavaScriptResult);
        JLLatLng center = JLLatLng.builder().lat(52.5200).lng(13.4050).build();

        JLCircle circle1 = vectorLayer.addCircle(center);
        JLCircle circle2 = vectorLayer.addCircle(center);

        assertThat(circle1.getId()).isNotEqualTo(circle2.getId());
        assertThat(circle1.getId()).startsWith("JLCircle");
        assertThat(circle2.getId()).startsWith("JLCircle");
    }
}