package io.github.makbn.jlmap.fx.test.layer;

import io.github.makbn.jlmap.JLMapCallbackHandler;
import io.github.makbn.jlmap.engine.JLWebEngine;
import io.github.makbn.jlmap.fx.layer.JLControlLayer;
import io.github.makbn.jlmap.model.JLBounds;
import io.github.makbn.jlmap.model.JLLatLng;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.argThat;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class JLControlLayerTest {

    @Mock
    private JLWebEngine<Object> engine;

    @Mock
    private JLMapCallbackHandler callbackHandler;

    private JLControlLayer controlLayer;

    @BeforeEach
    void setUp() {
        controlLayer = new JLControlLayer(engine, callbackHandler);
    }

    // === Constructor Tests ===

    @Test
    void constructor_withNullEngine_shouldAcceptNullEngine() {
        // When/Then - Constructor validation is not implemented in the actual class
        // This test documents the current behavior
        JLControlLayer layer = new JLControlLayer(null, callbackHandler);
        assertThat(layer).isNotNull();
    }

    @Test
    void constructor_withNullCallbackHandler_shouldAcceptNullHandler() {
        // When/Then - Constructor validation is not implemented in the actual class
        // This test documents the current behavior
        JLControlLayer layer = new JLControlLayer(engine, null);
        assertThat(layer).isNotNull();
    }

    @Test
    void controlLayer_shouldExtendJLLayer() {
        // This test verifies the inheritance hierarchy
        Class<?> layerClass = JLControlLayer.class;

        // Then
        assertThat(layerClass.getSuperclass().getSimpleName()).isEqualTo("JLLayer");
    }

    // === Zoom Tests ===

    @Test
    void zoomIn_shouldExecuteCorrectScript() {
        // Given
        int delta = 2;

        // When
        controlLayer.zoomIn(delta);

        // Then
        verify(engine).executeScript("this.map.zoomIn(2)");
    }

    @Test
    void zoomOut_shouldExecuteCorrectScript() {
        // Given
        int delta = 3;

        // When
        controlLayer.zoomOut(delta);

        // Then
        verify(engine).executeScript("this.map.zoomOut(3)");
    }

    @Test
    void setZoom_shouldExecuteCorrectScript() {
        // Given
        int zoomLevel = 10;

        // When
        controlLayer.setZoom(zoomLevel);

        // Then
        verify(engine).executeScript("this.map.setZoom(10)");
    }

    @Test
    void setZoomAround_shouldExecuteCorrectScript() {
        // Given
        JLLatLng latLng = JLLatLng.builder().lat(52.5200).lng(13.4050).build();
        int zoom = 15;

        // When
        controlLayer.setZoomAround(latLng, zoom);

        // Then
        verify(engine).executeScript("this.map.setZoomAround(L.latLng(52.520000, 13.405000), 15)");
    }

    @Test
    void setZoomAround_withNullLatLng_shouldThrowNullPointerException() {
        // When/Then
        assertThatThrownBy(() -> controlLayer.setZoomAround(null, 10))
                .isInstanceOf(NullPointerException.class);
    }

    // === Bounds Tests ===

    @Test
    void fitBounds_shouldExecuteCorrectScript() {
        // Given
        JLBounds bounds = JLBounds.builder()
                .southWest(JLLatLng.builder().lat(52.5200).lng(13.4050).build())
                .northEast(JLLatLng.builder().lat(52.5300).lng(13.4150).build())
                .build();

        // When
        controlLayer.fitBounds(bounds);

        // Then
        verify(engine).executeScript(argThat(script ->
                script.startsWith("this.map.fitBounds(") &&
                        script.contains(bounds.toString())));
    }

    @Test
    void fitBounds_withNullBounds_shouldThrowNullPointerException() {
        // When/Then
        assertThatThrownBy(() -> controlLayer.fitBounds(null))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    void fitWorld_shouldExecuteCorrectScript() {
        // When
        controlLayer.fitWorld();

        // Then
        verify(engine).executeScript("this.map.fitWorld()");
    }

    @Test
    void setMaxBounds_shouldExecuteCorrectScript() {
        // Given
        JLBounds bounds = JLBounds.builder()
                .southWest(JLLatLng.builder().lat(45.0).lng(-120.0).build())
                .northEast(JLLatLng.builder().lat(50.0).lng(-110.0).build())
                .build();

        // When
        controlLayer.setMaxBounds(bounds);

        // Then
        verify(engine).executeScript(argThat(script ->
                script.startsWith("this.map.setMaxBounds(") &&
                        script.contains(bounds.toString())));
    }

    @Test
    void setMaxBounds_withNullBounds_shouldThrowNullPointerException() {
        // When/Then
        assertThatThrownBy(() -> controlLayer.setMaxBounds(null))
                .isInstanceOf(NullPointerException.class);
    }

    // === Pan Tests ===

    @Test
    void panTo_shouldExecuteCorrectScript() {
        // Given
        JLLatLng latLng = JLLatLng.builder().lat(52.5200).lng(13.4050).build();

        // When
        controlLayer.panTo(latLng);

        // Then
        verify(engine).executeScript("this.map.panTo(L.latLng(52.520000, 13.405000))");
    }

    @Test
    void panTo_withNullLatLng_shouldThrowNullPointerException() {
        // When/Then
        assertThatThrownBy(() -> controlLayer.panTo(null))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    void flyTo_shouldExecuteCorrectScript() {
        // Given
        JLLatLng latLng = JLLatLng.builder().lat(52.5200).lng(13.4050).build();
        int zoom = 12;

        // When
        controlLayer.flyTo(latLng, zoom);

        // Then
        verify(engine).executeScript("this.map.flyTo(L.latLng(52.520000, 13.405000), 12)");
    }

    @Test
    void flyTo_withNullLatLng_shouldThrowNullPointerException() {
        // When/Then
        assertThatThrownBy(() -> controlLayer.flyTo(null, 10))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    void flyToBounds_shouldExecuteCorrectScript() {
        // Given
        JLBounds bounds = JLBounds.builder()
                .southWest(JLLatLng.builder().lat(52.5200).lng(13.4050).build())
                .northEast(JLLatLng.builder().lat(52.5300).lng(13.4150).build())
                .build();

        // When
        controlLayer.flyToBounds(bounds);

        // Then
        verify(engine).executeScript(argThat(script ->
                script.startsWith("this.map.flyToBounds(") &&
                        script.contains(bounds.toString())));
    }

    @Test
    void panInsideBounds_shouldExecuteCorrectScript() {
        // Given
        JLBounds bounds = JLBounds.builder()
                .southWest(JLLatLng.builder().lat(52.5200).lng(13.4050).build())
                .northEast(JLLatLng.builder().lat(52.5300).lng(13.4150).build())
                .build();

        // When
        controlLayer.panInsideBounds(bounds);

        // Then
        verify(engine).executeScript(argThat(script ->
                script.startsWith("this.map.panInsideBounds(") &&
                        script.contains(bounds.toString())));
    }

    @Test
    void panInside_shouldExecuteCorrectScript() {
        // Given
        JLLatLng latLng = JLLatLng.builder().lat(52.5200).lng(13.4050).build();

        // When
        controlLayer.panInside(latLng);

        // Then
        verify(engine).executeScript("this.map.panInside(L.latLng(52.520000, 13.405000))");
    }

    // === Zoom Limits Tests ===

    @Test
    void setMinZoom_shouldExecuteCorrectScript() {
        // Given
        int zoom = 5;

        // When
        controlLayer.setMinZoom(zoom);

        // Then
        verify(engine).executeScript("this.map.setMinZoom(5)");
    }

    @Test
    void setMaxZoom_shouldExecuteCorrectScript() {
        // Given
        int zoom = 18;

        // When
        controlLayer.setMaxZoom(zoom);

        // Then
        verify(engine).executeScript("this.map.setMaxZoom(18)");
    }

    // === Edge Cases ===

    @Test
    void zoomOperations_withZeroValues_shouldExecuteCorrectScript() {
        // When
        controlLayer.zoomIn(0);
        controlLayer.zoomOut(0);
        controlLayer.setZoom(0);

        // Then
        verify(engine).executeScript("this.map.zoomIn(0)");
        verify(engine).executeScript("this.map.zoomOut(0)");
        verify(engine).executeScript("this.map.setZoom(0)");
    }

    @Test
    void zoomOperations_withNegativeValues_shouldExecuteCorrectScript() {
        // When
        controlLayer.zoomIn(-1);
        controlLayer.zoomOut(-2);
        controlLayer.setZoom(-5);

        // Then
        verify(engine).executeScript("this.map.zoomIn(-1)");
        verify(engine).executeScript("this.map.zoomOut(-2)");
        verify(engine).executeScript("this.map.setZoom(-5)");
    }

    @Test
    void multipleOperations_shouldExecuteAllScripts() {
        // Given
        JLLatLng center = JLLatLng.builder().lat(52.5200).lng(13.4050).build();
        int zoom = 10;

        // When
        controlLayer.setZoom(zoom);
        controlLayer.panTo(center);
        controlLayer.setMinZoom(5);
        controlLayer.setMaxZoom(18);

        // Then
        verify(engine).executeScript("this.map.setZoom(10)");
        verify(engine).executeScript("this.map.panTo(L.latLng(52.520000, 13.405000))");
        verify(engine).executeScript("this.map.setMinZoom(5)");
        verify(engine).executeScript("this.map.setMaxZoom(18)");
    }
}