package io.github.makbn.jlmap.vaadin.test.layer;

import com.vaadin.flow.component.page.PendingJavaScriptResult;
import io.github.makbn.jlmap.JLMapCallbackHandler;
import io.github.makbn.jlmap.engine.JLWebEngine;
import io.github.makbn.jlmap.model.JLBounds;
import io.github.makbn.jlmap.model.JLLatLng;
import io.github.makbn.jlmap.vaadin.layer.JLVaadinControlLayer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JLVaadinControlLayerTest {

    @Mock
    private JLWebEngine<PendingJavaScriptResult> mockEngine;

    @Mock
    private JLMapCallbackHandler mockCallbackHandler;

    @Mock
    private PendingJavaScriptResult mockJavaScriptResult;

    private JLVaadinControlLayer controlLayer;

    @BeforeEach
    void setUp() {
        controlLayer = new JLVaadinControlLayer(mockEngine, mockCallbackHandler);
    }

    // === Constructor Tests ===

    @Test
    void constructor_withNullEngine_shouldAcceptNullEngine() {
        JLVaadinControlLayer layer = new JLVaadinControlLayer(null, mockCallbackHandler);
        assertThat(layer).isNotNull();
    }

    @Test
    void constructor_withNullCallbackHandler_shouldAcceptNullHandler() {
        JLVaadinControlLayer layer = new JLVaadinControlLayer(mockEngine, null);
        assertThat(layer).isNotNull();
    }

    @Test
    void controlLayer_shouldExtendJLLayer() {
        assertThat(controlLayer).isInstanceOf(io.github.makbn.jlmap.vaadin.layer.JLVaadinLayer.class);
    }

    // === Zoom Tests ===

    @Test
    void zoomIn_shouldExecuteCorrectScript() {
        when(mockEngine.executeScript(anyString())).thenReturn(mockJavaScriptResult);
        int delta = 2;

        controlLayer.zoomIn(delta);

        verify(mockEngine).executeScript("this.map.zoomIn(2)");
    }

    @Test
    void zoomOut_shouldExecuteCorrectScript() {
        when(mockEngine.executeScript(anyString())).thenReturn(mockJavaScriptResult);
        int delta = 3;

        controlLayer.zoomOut(delta);

        verify(mockEngine).executeScript("this.map.zoomOut(3)");
    }

    @Test
    void setZoom_shouldExecuteCorrectScript() {
        when(mockEngine.executeScript(anyString())).thenReturn(mockJavaScriptResult);
        int zoomLevel = 10;

        controlLayer.setZoom(zoomLevel);

        verify(mockEngine).executeScript("this.map.setZoom(10)");
    }

    @Test
    void setZoomAround_shouldExecuteCorrectScript() {
        when(mockEngine.executeScript(anyString())).thenReturn(mockJavaScriptResult);
        JLLatLng latLng = JLLatLng.builder().lat(52.5200).lng(13.4050).build();
        int zoom = 15;

        controlLayer.setZoomAround(latLng, zoom);

        verify(mockEngine).executeScript("this.map.setZoomAround(L.latLng(52.520000, 13.405000), 15)");
    }

    @Test
    void setZoomAround_withNullLatLng_shouldThrowNullPointerException() {
        assertThatThrownBy(() -> controlLayer.setZoomAround(null, 10))
                .isInstanceOf(NullPointerException.class);
    }

    // === Bounds Tests ===

    @Test
    void fitBounds_shouldExecuteCorrectScript() {
        when(mockEngine.executeScript(anyString())).thenReturn(mockJavaScriptResult);
        JLBounds bounds = JLBounds.builder()
                .southWest(JLLatLng.builder().lat(52.5200).lng(13.4050).build())
                .northEast(JLLatLng.builder().lat(52.5300).lng(13.4150).build())
                .build();

        controlLayer.fitBounds(bounds);

        verify(mockEngine).executeScript(argThat(script ->
                script.startsWith("this.map.fitBounds(") &&
                        script.contains(bounds.toString())));
    }

    @Test
    void fitBounds_withNullBounds_shouldThrowNullPointerException() {
        assertThatThrownBy(() -> controlLayer.fitBounds(null))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    void fitWorld_shouldExecuteCorrectScript() {
        when(mockEngine.executeScript(anyString())).thenReturn(mockJavaScriptResult);

        controlLayer.fitWorld();

        verify(mockEngine).executeScript("this.map.fitWorld()");
    }

    @Test
    void setMaxBounds_shouldExecuteCorrectScript() {
        when(mockEngine.executeScript(anyString())).thenReturn(mockJavaScriptResult);
        JLBounds bounds = JLBounds.builder()
                .southWest(JLLatLng.builder().lat(45.0).lng(-120.0).build())
                .northEast(JLLatLng.builder().lat(50.0).lng(-110.0).build())
                .build();

        controlLayer.setMaxBounds(bounds);

        verify(mockEngine).executeScript(argThat(script ->
                script.startsWith("this.map.setMaxBounds(") &&
                        script.contains(bounds.toString())));
    }

    @Test
    void setMaxBounds_withNullBounds_shouldThrowNullPointerException() {
        assertThatThrownBy(() -> controlLayer.setMaxBounds(null))
                .isInstanceOf(NullPointerException.class);
    }

    // === Pan Tests ===

    @Test
    void panTo_shouldExecuteCorrectScript() {
        when(mockEngine.executeScript(anyString())).thenReturn(mockJavaScriptResult);
        JLLatLng latLng = JLLatLng.builder().lat(52.5200).lng(13.4050).build();

        controlLayer.panTo(latLng);

        verify(mockEngine).executeScript("this.map.panTo(L.latLng(52.520000, 13.405000))");
    }

    @Test
    void panTo_withNullLatLng_shouldThrowNullPointerException() {
        assertThatThrownBy(() -> controlLayer.panTo(null))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    void flyTo_shouldExecuteCorrectScript() {
        when(mockEngine.executeScript(anyString())).thenReturn(mockJavaScriptResult);
        JLLatLng latLng = JLLatLng.builder().lat(52.5200).lng(13.4050).build();
        int zoom = 12;

        controlLayer.flyTo(latLng, zoom);

        verify(mockEngine).executeScript("this.map.flyTo(L.latLng(52.520000, 13.405000), 12)");
    }

    @Test
    void flyTo_withNullLatLng_shouldThrowNullPointerException() {
        assertThatThrownBy(() -> controlLayer.flyTo(null, 10))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    void flyToBounds_shouldExecuteCorrectScript() {
        when(mockEngine.executeScript(anyString())).thenReturn(mockJavaScriptResult);
        JLBounds bounds = JLBounds.builder()
                .southWest(JLLatLng.builder().lat(52.5200).lng(13.4050).build())
                .northEast(JLLatLng.builder().lat(52.5300).lng(13.4150).build())
                .build();

        controlLayer.flyToBounds(bounds);

        verify(mockEngine).executeScript(argThat(script ->
                script.startsWith("this.map.flyToBounds(") &&
                        script.contains(bounds.toString())));
    }

    @Test
    void panInsideBounds_shouldExecuteCorrectScript() {
        when(mockEngine.executeScript(anyString())).thenReturn(mockJavaScriptResult);
        JLBounds bounds = JLBounds.builder()
                .southWest(JLLatLng.builder().lat(52.5200).lng(13.4050).build())
                .northEast(JLLatLng.builder().lat(52.5300).lng(13.4150).build())
                .build();

        controlLayer.panInsideBounds(bounds);

        verify(mockEngine).executeScript(argThat(script ->
                script.startsWith("this.map.panInsideBounds(") &&
                        script.contains(bounds.toString())));
    }

    @Test
    void panInside_shouldExecuteCorrectScript() {
        when(mockEngine.executeScript(anyString())).thenReturn(mockJavaScriptResult);
        JLLatLng latLng = JLLatLng.builder().lat(52.5200).lng(13.4050).build();

        controlLayer.panInside(latLng);

        verify(mockEngine).executeScript("this.map.panInside(L.latLng(52.520000, 13.405000))");
    }

    // === Zoom Limits Tests ===

    @Test
    void setMinZoom_shouldExecuteCorrectScript() {
        when(mockEngine.executeScript(anyString())).thenReturn(mockJavaScriptResult);
        int zoom = 5;

        controlLayer.setMinZoom(zoom);

        verify(mockEngine).executeScript("this.map.setMinZoom(5)");
    }

    @Test
    void setMaxZoom_shouldExecuteCorrectScript() {
        when(mockEngine.executeScript(anyString())).thenReturn(mockJavaScriptResult);
        int zoom = 18;

        controlLayer.setMaxZoom(zoom);

        verify(mockEngine).executeScript("this.map.setMaxZoom(18)");
    }

    // === Edge Cases ===

    @Test
    void zoomOperations_withZeroValues_shouldExecuteCorrectScript() {
        when(mockEngine.executeScript(anyString())).thenReturn(mockJavaScriptResult);

        controlLayer.zoomIn(0);
        controlLayer.zoomOut(0);
        controlLayer.setZoom(0);

        verify(mockEngine).executeScript("this.map.zoomIn(0)");
        verify(mockEngine).executeScript("this.map.zoomOut(0)");
        verify(mockEngine).executeScript("this.map.setZoom(0)");
    }

    @Test
    void zoomOperations_withNegativeValues_shouldExecuteCorrectScript() {
        when(mockEngine.executeScript(anyString())).thenReturn(mockJavaScriptResult);

        controlLayer.zoomIn(-1);
        controlLayer.zoomOut(-2);
        controlLayer.setZoom(-5);

        verify(mockEngine).executeScript("this.map.zoomIn(-1)");
        verify(mockEngine).executeScript("this.map.zoomOut(-2)");
        verify(mockEngine).executeScript("this.map.setZoom(-5)");
    }

    @Test
    void multipleOperations_shouldExecuteAllScripts() {
        when(mockEngine.executeScript(anyString())).thenReturn(mockJavaScriptResult);
        JLLatLng center = JLLatLng.builder().lat(52.5200).lng(13.4050).build();
        int zoom = 10;

        controlLayer.setZoom(zoom);
        controlLayer.panTo(center);
        controlLayer.setMinZoom(5);
        controlLayer.setMaxZoom(18);

        verify(mockEngine).executeScript("this.map.setZoom(10)");
        verify(mockEngine).executeScript("this.map.panTo(L.latLng(52.520000, 13.405000))");
        verify(mockEngine).executeScript("this.map.setMinZoom(5)");
        verify(mockEngine).executeScript("this.map.setMaxZoom(18)");
    }
}