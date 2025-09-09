package io.github.makbn.jlmap.fx.test.layer;

import io.github.makbn.jlmap.JLMapCallbackHandler;
import io.github.makbn.jlmap.engine.JLWebEngine;
import io.github.makbn.jlmap.fx.layer.JLUiLayer;
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
class JLUiLayerTest {

    @Mock
    private JLWebEngine<Object> engine;

    @Mock
    private JLMapCallbackHandler callbackHandler;

    private JLUiLayer uiLayer;

    @BeforeEach
    void setUp() {
        uiLayer = new JLUiLayer(engine, callbackHandler);
    }

    // === Constructor Tests ===

    @Test
    void constructor_withNullEngine_shouldAcceptNullEngine() {
        // When/Then - Constructor validation is not implemented in the actual class
        // This test documents the current behavior
        JLUiLayer layer = new JLUiLayer(null, callbackHandler);
        assertThat(layer).isNotNull();
    }

    @Test
    void constructor_withNullCallbackHandler_shouldAcceptNullHandler() {
        // When/Then - Constructor validation is not implemented in the actual class
        // This test documents the current behavior
        JLUiLayer layer = new JLUiLayer(engine, null);
        assertThat(layer).isNotNull();
    }

    @Test
    void uiLayer_shouldExtendJLLayer() {
        // This test verifies the inheritance hierarchy
        Class<?> layerClass = JLUiLayer.class;

        // Then
        assertThat(layerClass.getSuperclass().getSimpleName()).isEqualTo("JLLayer");
    }

    // === Marker Tests ===

    @Test
    void addMarker_withNonDraggableMarker_shouldExecuteCorrectScript() {
        // Given
        JLLatLng position = JLLatLng.builder().lat(52.5200).lng(13.4050).build();
        String text = "Test Marker";
        boolean draggable = false;

        // When
        JLMarker result = uiLayer.addMarker(position, text, draggable);

        // Then
        ArgumentCaptor<String> scriptCaptor = ArgumentCaptor.forClass(String.class);
        verify(engine).executeScript(scriptCaptor.capture());
        verify(callbackHandler).addJLObject(anyString(), eq(result));

        String script = scriptCaptor.getValue();
        assertThat(script).contains("L.marker");
        assertThat(script).contains("[52.520000, 13.405000]");
        assertThat(script).contains("draggable: false");
        assertThat(script).contains("addTo(this.map)");
        assertThat(script).contains("on('move'");
        assertThat(script).contains("on('click'");

        assertThat(result).isNotNull();
        assertThat(result.getId()).startsWith("JLGeoJson");
        assertThat(result.getText()).isEqualTo(text);
    }

    @Test
    void addMarker_withDraggableMarker_shouldExecuteScriptWithDraggableTrue() {
        // Given
        JLLatLng position = JLLatLng.builder().lat(52.5200).lng(13.4050).build();
        String text = "Draggable Marker";
        boolean draggable = true;

        // When
        JLMarker result = uiLayer.addMarker(position, text, draggable);

        // Then
        ArgumentCaptor<String> scriptCaptor = ArgumentCaptor.forClass(String.class);
        verify(engine).executeScript(scriptCaptor.capture());

        String script = scriptCaptor.getValue();
        assertThat(script).contains("draggable: true");

        assertThat(result).isNotNull();
        assertThat(result.getText()).isEqualTo(text);
    }

    @Test
    void addMarker_withNullPosition_shouldThrowNullPointerException() {
        // When/Then
        assertThatThrownBy(() -> uiLayer.addMarker(null, "Test", false))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    void addMarker_withNullText_shouldAcceptNullText() {
        // Given
        JLLatLng position = JLLatLng.builder().lat(52.5200).lng(13.4050).build();

        // When/Then - Null text validation is not implemented in the actual class
        // This test documents the current behavior
        JLMarker marker = uiLayer.addMarker(position, null, false);
        assertThat(marker).isNotNull();
    }

    @Test
    void removeMarker_shouldExecuteRemoveScript() {
        // Given
        String markerId = "testMarkerId";

        // When
        boolean result = uiLayer.removeMarker(markerId);

        // Then
        verify(engine).executeScript("this.map.removeLayer(this.testMarkerId)");
        verify(callbackHandler).remove(JLMarker.class, markerId);
        assertThat(result).isTrue();
    }

    // === Popup Tests ===

    @Test
    void addPopup_withDefaultOptions_shouldExecuteCorrectScript() {
        // Given
        JLLatLng position = JLLatLng.builder().lat(52.5200).lng(13.4050).build();
        String text = "Test Popup";

        // When
        JLPopup result = uiLayer.addPopup(position, text);

        // Then
        ArgumentCaptor<String> scriptCaptor = ArgumentCaptor.forClass(String.class);
        verify(engine).executeScript(scriptCaptor.capture());
        verify(callbackHandler).addJLObject(anyString(), eq(result));

        String script = scriptCaptor.getValue();
        assertThat(script).contains("L.popup");
        assertThat(script).contains("[52.520000, 13.405000]");
        assertThat(script).contains("addTo(this.map)");

        assertThat(result).isNotNull();
        assertThat(result.getId()).startsWith("JLGeoJson");
        assertThat(result.getText()).isEqualTo(text);
    }

    @Test
    void addPopup_withCustomOptions_shouldIncludeOptionsInScript() {
        // Given
        JLLatLng position = JLLatLng.builder().lat(52.5200).lng(13.4050).build();
        String text = "Custom Popup";
        JLOptions options = JLOptions.builder()
                .closeButton(false)
                .autoClose(false)
                .build();

        // When
        JLPopup result = uiLayer.addPopup(position, text, options);

        // Then
        ArgumentCaptor<String> scriptCaptor = ArgumentCaptor.forClass(String.class);
        verify(engine).executeScript(scriptCaptor.capture());

        String script = scriptCaptor.getValue();
        assertThat(script).contains("closeButton: false");
        assertThat(script).contains("autoClose: false");

        assertThat(result).isNotNull();
        assertThat(result.getText()).isEqualTo(text);
    }

    @Test
    void addPopup_withNullPosition_shouldThrowNullPointerException() {
        // When/Then
        assertThatThrownBy(() -> uiLayer.addPopup(null, "Test"))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    void addPopup_withNullText_shouldAcceptNullText() {
        // Given
        JLLatLng position = JLLatLng.builder().lat(52.5200).lng(13.4050).build();

        // When/Then - Null text validation is not implemented in the actual class
        // This test documents the current behavior
        JLPopup popup = uiLayer.addPopup(position, null);
        assertThat(popup).isNotNull();
    }

    @Test
    void removePopup_shouldExecuteRemoveScript() {
        // Given
        String popupId = "testPopupId";

        // When
        boolean result = uiLayer.removePopup(popupId);

        // Then
        verify(engine).executeScript("this.map.removeLayer(this.testPopupId)");
        verify(callbackHandler).remove(JLPopup.class, popupId);
        assertThat(result).isTrue();
    }

    // === Image Overlay Tests ===

    @Test
    void addImage_shouldExecuteCorrectScript() {
        // Given
        JLBounds bounds = JLBounds.builder()
                .southWest(JLLatLng.builder().lat(52.5200).lng(13.4050).build())
                .northEast(JLLatLng.builder().lat(52.5300).lng(13.4150).build())
                .build();
        String imageUrl = "https://example.com/image.png";
        JLOptions options = JLOptions.DEFAULT;

        // When
        JLImageOverlay result = uiLayer.addImage(bounds, imageUrl, options);

        // Then
        ArgumentCaptor<String> scriptCaptor = ArgumentCaptor.forClass(String.class);
        verify(engine).executeScript(scriptCaptor.capture());
        verify(callbackHandler).addJLObject(anyString(), eq(result));

        String script = scriptCaptor.getValue();
        assertThat(script).contains("L.imageOverlay");
        assertThat(script).contains("https://example.com/image.png");
        assertThat(script).contains("[52.520000, 13.405000]");
        assertThat(script).contains("[52.530000, 13.415000]");
        assertThat(script).contains("addTo(this.map)");

        assertThat(result).isNotNull();
        assertThat(result.getId()).startsWith("JLGeoJson");
        assertThat(result.getImageUrl()).isEqualTo(imageUrl);
    }

    @Test
    void addImage_withNullBounds_shouldThrowNullPointerException() {
        // When/Then
        assertThatThrownBy(() -> uiLayer.addImage(null, "https://example.com/image.png", JLOptions.DEFAULT))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    void addImage_withNullImageUrl_shouldAcceptNullUrl() {
        // Given
        JLBounds bounds = JLBounds.builder()
                .southWest(JLLatLng.builder().lat(52.5200).lng(13.4050).build())
                .northEast(JLLatLng.builder().lat(52.5300).lng(13.4150).build())
                .build();

        // When/Then - Null URL validation is not implemented in the actual class
        // This test documents the current behavior
        JLImageOverlay image = uiLayer.addImage(bounds, null, JLOptions.DEFAULT);
        assertThat(image).isNotNull();
    }

    // === ID Generation Tests ===

    @Test
    void uiLayer_shouldGenerateUniqueIds() {
        // Given
        JLLatLng position = JLLatLng.builder().lat(52.5200).lng(13.4050).build();

        // When
        JLMarker marker1 = uiLayer.addMarker(position, "Marker 1", false);
        JLMarker marker2 = uiLayer.addMarker(position, "Marker 2", false);

        // Then
        assertThat(marker1.getId()).isNotEqualTo(marker2.getId());
        assertThat(marker1.getId()).startsWith("JLGeoJson");
        assertThat(marker2.getId()).startsWith("JLGeoJson");
    }
}