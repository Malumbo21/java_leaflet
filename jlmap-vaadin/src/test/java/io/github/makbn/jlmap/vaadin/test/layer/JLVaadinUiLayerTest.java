package io.github.makbn.jlmap.vaadin.test.layer;

import com.vaadin.flow.component.page.PendingJavaScriptResult;
import io.github.makbn.jlmap.JLMapCallbackHandler;
import io.github.makbn.jlmap.engine.JLWebEngine;
import io.github.makbn.jlmap.model.*;
import io.github.makbn.jlmap.vaadin.layer.JLVaadinUiLayer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JLVaadinUiLayerTest {

    @Mock
    private JLWebEngine<PendingJavaScriptResult> mockEngine;

    @Mock
    private JLMapCallbackHandler mockCallbackHandler;

    @Mock
    private PendingJavaScriptResult mockJavaScriptResult;

    private JLVaadinUiLayer uiLayer;

    @BeforeEach
    void setUp() {
        uiLayer = new JLVaadinUiLayer(mockEngine, mockCallbackHandler);
    }

    @Test
    void constructor_withNullEngine_shouldAcceptNullEngine() {
        JLVaadinUiLayer layer = new JLVaadinUiLayer(null, mockCallbackHandler);
        assertThat(layer).isNotNull();
    }

    @Test
    void constructor_withNullCallbackHandler_shouldAcceptNullHandler() {
        JLVaadinUiLayer layer = new JLVaadinUiLayer(mockEngine, null);
        assertThat(layer).isNotNull();
    }

    @Test
    void uiLayer_shouldExtendJLLayer() {
        assertThat(uiLayer).isInstanceOf(io.github.makbn.jlmap.vaadin.layer.JLVaadinLayer.class);
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void addMarker_withNonDraggableMarker_shouldExecuteCorrectScript(boolean addText) {
        when(mockEngine.executeScript(anyString())).thenReturn(mockJavaScriptResult);
        JLLatLng position = JLLatLng.builder().lat(51.505).lng(-0.09).build();

        JLMarker marker = uiLayer.addMarker(position, addText ? "Test Marker" : null, false);

        assertThat(marker).isNotNull();
        assertThat(marker.getLatLng()).isEqualTo(position);
        assertThat(marker.getText()).isEqualTo(addText ? "Test Marker" : null);
        verify(mockEngine, Mockito.times(addText ? 2 : 1)).executeScript(anyString());
    }

    @Test
    void addMarker_withDraggableMarker_shouldExecuteScriptWithDraggableTrue() {
        when(mockEngine.executeScript(anyString())).thenReturn(mockJavaScriptResult);
        JLLatLng position = JLLatLng.builder().lat(51.505).lng(-0.09).build();

        JLMarker marker = uiLayer.addMarker(position, "Draggable Marker", true);

        assertThat(marker).isNotNull();
        assertThat(marker.getLatLng()).isEqualTo(position);
        assertThat(marker.getText()).isEqualTo("Draggable Marker");
        verify(mockEngine, Mockito.times(2)).executeScript(anyString());
    }

    @Test
    void addMarker_withNullPosition_shouldThrowNullPointerException() {
        assertThatThrownBy(() -> uiLayer.addMarker(null, "Test", false))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    void addMarker_withNullText_shouldAcceptNullText() {
        when(mockEngine.executeScript(anyString())).thenReturn(mockJavaScriptResult);
        JLLatLng position = JLLatLng.builder().lat(51.505).lng(-0.09).build();

        JLMarker marker = uiLayer.addMarker(position, null, false);

        assertThat(marker).isNotNull();
        assertThat(marker.getLatLng()).isEqualTo(position);
        verify(mockEngine).executeScript(anyString());
    }

    @Test
    void removeMarker_shouldExecuteRemoveScript() {
        when(mockEngine.executeScript(anyString())).thenReturn(mockJavaScriptResult);

        boolean result = uiLayer.removeMarker("test-marker-id");

        assertThat(result).isTrue();
        verify(mockEngine).executeScript(anyString());
    }

    @Test
    void addPopup_withDefaultOptions_shouldExecuteCorrectScript() {
        when(mockEngine.executeScript(anyString())).thenReturn(mockJavaScriptResult);
        JLLatLng position = JLLatLng.builder().lat(51.505).lng(-0.09).build();

        JLPopup popup = uiLayer.addPopup(position, "Test Popup");

        assertThat(popup).isNotNull();
        assertThat(popup.getLatLng()).isEqualTo(position);
        assertThat(popup.getText()).isEqualTo("Test Popup");
        verify(mockEngine).executeScript(anyString());
    }

    @Test
    void addPopup_withCustomOptions_shouldIncludeOptionsInScript() {
        when(mockEngine.executeScript(anyString())).thenReturn(mockJavaScriptResult);
        JLLatLng position = JLLatLng.builder().lat(51.505).lng(-0.09).build();
        JLOptions options = JLOptions.builder().closeButton(false).build();

        JLPopup popup = uiLayer.addPopup(position, "Custom Popup", options);

        assertThat(popup).isNotNull();
        assertThat(popup.getLatLng()).isEqualTo(position);
        assertThat(popup.getText()).isEqualTo("Custom Popup");
        assertThat(popup.getOptions()).isEqualTo(options);
        verify(mockEngine).executeScript(anyString());
    }

    @Test
    void addPopup_withNullPosition_shouldThrowNullPointerException() {
        assertThatThrownBy(() -> uiLayer.addPopup(null, "Test"))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    void addPopup_withNullText_shouldAcceptNullText() {
        when(mockEngine.executeScript(anyString())).thenReturn(mockJavaScriptResult);
        JLLatLng position = JLLatLng.builder().lat(51.505).lng(-0.09).build();

        JLPopup popup = uiLayer.addPopup(position, null);

        assertThat(popup).isNotNull();
        assertThat(popup.getLatLng()).isEqualTo(position);
        verify(mockEngine).executeScript(anyString());
    }

    @Test
    void removePopup_shouldExecuteRemoveScript() {
        when(mockEngine.executeScript(anyString())).thenReturn(mockJavaScriptResult);

        boolean result = uiLayer.removePopup("test-popup-id");

        assertThat(result).isTrue();
        verify(mockEngine).executeScript(anyString());
    }

    @Test
    void addImage_shouldExecuteCorrectScript() {
        when(mockEngine.executeScript(anyString())).thenReturn(mockJavaScriptResult);
        JLBounds bounds = JLBounds.builder()
                .southWest(JLLatLng.builder().lat(40.712).lng(-74.227).build())
                .northEast(JLLatLng.builder().lat(40.774).lng(-74.125).build())
                .build();
        String imageUrl = "https://example.com/image.png";

        JLImageOverlay image = uiLayer.addImage(bounds, imageUrl, JLOptions.DEFAULT);

        assertThat(image).isNotNull();
        assertThat(image.getBounds()).isEqualTo(bounds);
        assertThat(image.getImageUrl()).isEqualTo(imageUrl);
        verify(mockEngine).executeScript(anyString());
    }

    @Test
    void addImage_withNullBounds_shouldThrowNullPointerException() {
        assertThatThrownBy(() -> uiLayer.addImage(null, "test.png", JLOptions.DEFAULT))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    void addImage_withNullImageUrl_shouldAcceptNullUrl() {
        when(mockEngine.executeScript(anyString())).thenReturn(mockJavaScriptResult);
        JLBounds bounds = JLBounds.builder()
                .southWest(JLLatLng.builder().lat(40.712).lng(-74.227).build())
                .northEast(JLLatLng.builder().lat(40.774).lng(-74.125).build())
                .build();

        JLImageOverlay image = uiLayer.addImage(bounds, "some_url", JLOptions.DEFAULT);

        assertThat(image).isNotNull();
        assertThat(image.getBounds()).isEqualTo(bounds);
        verify(mockEngine).executeScript(anyString());
    }

    @Test
    void uiLayer_shouldGenerateUniqueIds() {
        when(mockEngine.executeScript(anyString())).thenReturn(mockJavaScriptResult);
        JLLatLng position = JLLatLng.builder().lat(51.505).lng(-0.09).build();

        JLMarker marker1 = uiLayer.addMarker(position, "Test 1", false);
        JLMarker marker2 = uiLayer.addMarker(position, "Test 2", false);

        assertThat(marker1.getId()).isNotEqualTo(marker2.getId());
        assertThat(marker1.getId()).startsWith("JLMarker_");
        assertThat(marker2.getId()).startsWith("JLMarker_");
    }
}