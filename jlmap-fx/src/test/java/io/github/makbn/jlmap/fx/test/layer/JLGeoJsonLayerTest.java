package io.github.makbn.jlmap.fx.test.layer;

import io.github.makbn.jlmap.JLMapCallbackHandler;
import io.github.makbn.jlmap.engine.JLWebEngine;
import io.github.makbn.jlmap.exception.JLException;
import io.github.makbn.jlmap.fx.layer.JLGeoJsonLayer;
import io.github.makbn.jlmap.geojson.JLGeoJsonContent;
import io.github.makbn.jlmap.geojson.JLGeoJsonFile;
import io.github.makbn.jlmap.geojson.JLGeoJsonURL;
import io.github.makbn.jlmap.model.JLGeoJson;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.File;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JLGeoJsonLayerTest {

    private static final String VALID_GEOJSON = """
            {
                "type": "FeatureCollection",
                "features": [
                    {
                        "type": "Feature",
                        "geometry": {
                            "type": "Point",
                            "coordinates": [13.4050, 52.5200]
                        },
                        "properties": {
                            "name": "Berlin"
                        }
                    }
                ]
            }""";
    @Mock
    private JLWebEngine<Object> engine;
    @Mock
    private JLMapCallbackHandler callbackHandler;
    @Mock
    private JLGeoJsonFile mockGeoJsonFile;
    @Mock
    private JLGeoJsonURL mockGeoJsonURL;
    @Mock
    private JLGeoJsonContent mockGeoJsonContent;
    private JLGeoJsonLayer geoJsonLayer;

    @BeforeEach
    void setUp() {
        geoJsonLayer = new JLGeoJsonLayer(engine, callbackHandler);

        // Use reflection to inject mocks for testing
        try {
            var fromFileField = JLGeoJsonLayer.class.getDeclaredField("fromFile");
            fromFileField.setAccessible(true);
            fromFileField.set(geoJsonLayer, mockGeoJsonFile);

            var fromUrlField = JLGeoJsonLayer.class.getDeclaredField("fromUrl");
            fromUrlField.setAccessible(true);
            fromUrlField.set(geoJsonLayer, mockGeoJsonURL);

            var fromContentField = JLGeoJsonLayer.class.getDeclaredField("fromContent");
            fromContentField.setAccessible(true);
            fromContentField.set(geoJsonLayer, mockGeoJsonContent);
        } catch (Exception e) {
            throw new RuntimeException("Failed to set up mocks", e);
        }
    }

    // === Constructor Tests ===

    @Test
    void constructor_withNullEngine_shouldAcceptNullEngine() {
        // When/Then - Constructor validation is not implemented in the actual class
        // This test documents the current behavior
        JLGeoJsonLayer layer = new JLGeoJsonLayer(null, callbackHandler);
        assertThat(layer).isNotNull();
    }

    @Test
    void constructor_withNullCallbackHandler_shouldAcceptNullHandler() {
        // When/Then - Constructor validation is not implemented in the actual class
        // This test documents the current behavior
        JLGeoJsonLayer layer = new JLGeoJsonLayer(engine, null);
        assertThat(layer).isNotNull();
    }

    @Test
    void geoJsonLayer_shouldExtendJLLayer() {
        // This test verifies the inheritance hierarchy
        Class<?> layerClass = JLGeoJsonLayer.class;

        // Then
        assertThat(layerClass.getSuperclass().getSimpleName()).isEqualTo("JLLayer");
    }

    // === addFromFile Tests ===

    @Test
    void addFromFile_withValidFile_shouldLoadFileAndExecuteScript() throws JLException {
        // Given
        File testFile = new File("test.geojson");
        when(mockGeoJsonFile.load(testFile)).thenReturn(VALID_GEOJSON);

        // When
        JLGeoJson result = geoJsonLayer.addFromFile(testFile);

        // Then
        verify(mockGeoJsonFile).load(testFile);
        ArgumentCaptor<String> scriptCaptor = ArgumentCaptor.forClass(String.class);
        verify(engine).executeScript(scriptCaptor.capture());
        verify(callbackHandler).addJLObject(anyString(), eq(result));

        String script = scriptCaptor.getValue();
        assertThat(script).contains("L.geoJSON");
        assertThat(script).contains("FeatureCollection");
        assertThat(script).contains("Berlin");
        assertThat(script).contains("addTo(this.map)");

        assertThat(result).isNotNull();
        assertThat(result.getId()).startsWith("JLGeoJson");
        assertThat(result.getGeoJsonContent()).isEqualTo(VALID_GEOJSON);
    }

    @Test
    void addFromFile_whenFileLoadFails_shouldThrowJLException() throws JLException {
        // Given
        File testFile = new File("invalid.geojson");
        JLException expectedException = new JLException("File not found");
        when(mockGeoJsonFile.load(testFile)).thenThrow(expectedException);

        // When/Then
        assertThatThrownBy(() -> geoJsonLayer.addFromFile(testFile))
                .isInstanceOf(JLException.class)
                .hasMessage("File not found");

        verify(mockGeoJsonFile).load(testFile);
        verifyNoInteractions(engine);
        verifyNoInteractions(callbackHandler);
    }

    @Test
    void addFromFile_withNullFile_shouldThrowNullPointerException() {
        // When/Then
        assertThatThrownBy(() -> geoJsonLayer.addFromFile(null))
                .isInstanceOf(NullPointerException.class);

        verifyNoInteractions(mockGeoJsonFile);
        verifyNoInteractions(engine);
        verifyNoInteractions(callbackHandler);
    }

    // === addFromUrl Tests ===

    @Test
    void addFromUrl_withValidUrl_shouldLoadUrlAndExecuteScript() throws JLException {
        // Given
        String testUrl = "https://example.com/data.geojson";
        String simpleGeoJson = """
                {
                    "type": "Point",
                    "coordinates": [13.4050, 52.5200]
                }""";
        when(mockGeoJsonURL.load(testUrl)).thenReturn(simpleGeoJson);

        // When
        JLGeoJson result = geoJsonLayer.addFromUrl(testUrl);

        // Then
        verify(mockGeoJsonURL).load(testUrl);
        ArgumentCaptor<String> scriptCaptor = ArgumentCaptor.forClass(String.class);
        verify(engine).executeScript(scriptCaptor.capture());
        verify(callbackHandler).addJLObject(anyString(), eq(result));

        String script = scriptCaptor.getValue();
        assertThat(script).contains("L.geoJSON");
        assertThat(script).contains("Point");
        assertThat(script).contains("13.4050, 52.5200");

        assertThat(result).isNotNull();
        assertThat(result.getId()).startsWith("JLGeoJson");
        assertThat(result.getGeoJsonContent()).isEqualTo(simpleGeoJson);
    }

    @Test
    void addFromUrl_whenUrlLoadFails_shouldThrowJLException() throws JLException {
        // Given
        String testUrl = "https://invalid-url.com/data.geojson";
        JLException expectedException = new JLException("URL not accessible");
        when(mockGeoJsonURL.load(testUrl)).thenThrow(expectedException);

        // When/Then
        assertThatThrownBy(() -> geoJsonLayer.addFromUrl(testUrl))
                .isInstanceOf(JLException.class)
                .hasMessage("URL not accessible");

        verify(mockGeoJsonURL).load(testUrl);
        verifyNoInteractions(engine);
        verifyNoInteractions(callbackHandler);
    }

    @Test
    void addFromUrl_withNullUrl_shouldThrowNullPointerException() {
        // When/Then
        assertThatThrownBy(() -> geoJsonLayer.addFromUrl(null))
                .isInstanceOf(NullPointerException.class);

        verifyNoInteractions(mockGeoJsonURL);
        verifyNoInteractions(engine);
        verifyNoInteractions(callbackHandler);
    }

    // === addFromContent Tests ===

    @Test
    void addFromContent_withValidContent_shouldLoadContentAndExecuteScript() throws JLException {
        // Given
        String content = VALID_GEOJSON;
        when(mockGeoJsonContent.load(content)).thenReturn(content);

        // When
        JLGeoJson result = geoJsonLayer.addFromContent(content);

        // Then
        verify(mockGeoJsonContent).load(content);
        ArgumentCaptor<String> scriptCaptor = ArgumentCaptor.forClass(String.class);
        verify(engine).executeScript(scriptCaptor.capture());
        verify(callbackHandler).addJLObject(anyString(), eq(result));

        String script = scriptCaptor.getValue();
        assertThat(script).contains("L.geoJSON");
        assertThat(script).contains("FeatureCollection");
        assertThat(script).contains("Berlin");

        assertThat(result).isNotNull();
        assertThat(result.getId()).startsWith("JLGeoJson");
        assertThat(result.getGeoJsonContent()).isEqualTo(content);
    }

    @Test
    void addFromContent_whenContentLoadFails_shouldThrowJLException() throws JLException {
        // Given
        String content = "invalid json";
        JLException expectedException = new JLException("Invalid GeoJSON");
        when(mockGeoJsonContent.load(content)).thenThrow(expectedException);

        // When/Then
        assertThatThrownBy(() -> geoJsonLayer.addFromContent(content))
                .isInstanceOf(JLException.class)
                .hasMessage("Invalid GeoJSON");

        verify(mockGeoJsonContent).load(content);
        verifyNoInteractions(engine);
        verifyNoInteractions(callbackHandler);
    }

    @Test
    void addFromContent_withNullContent_shouldThrowNullPointerException() {
        // When/Then
        assertThatThrownBy(() -> geoJsonLayer.addFromContent(null))
                .isInstanceOf(NullPointerException.class);

        verifyNoInteractions(mockGeoJsonContent);
        verifyNoInteractions(engine);
        verifyNoInteractions(callbackHandler);
    }

    // === removeGeoJson Tests ===

    @Test
    void removeGeoJson_shouldExecuteRemoveScriptAndRemoveFromCallback() {
        // Given
        String geoJsonId = "testGeoJsonId";

        // When
        boolean result = geoJsonLayer.removeGeoJson(geoJsonId);

        // Then
        verify(engine).executeScript("this.map.removeLayer(this.testGeoJsonId)");
        verify(callbackHandler).remove(JLGeoJson.class, geoJsonId);
        assertThat(result).isTrue();
    }

    @Test
    void removeGeoJson_withNullId_shouldThrowNullPointerException() {
        // When/Then
        assertThatThrownBy(() -> geoJsonLayer.removeGeoJson(null))
                .isInstanceOf(NullPointerException.class);

        verifyNoInteractions(engine);
        verifyNoInteractions(callbackHandler);
    }

    // === ID Generation Tests ===

    @Test
    void geoJsonLayer_shouldGenerateUniqueIds() throws JLException {
        // Given
        when(mockGeoJsonContent.load(anyString())).thenReturn(VALID_GEOJSON);

        // When
        JLGeoJson geoJson1 = geoJsonLayer.addFromContent(VALID_GEOJSON);
        JLGeoJson geoJson2 = geoJsonLayer.addFromContent(VALID_GEOJSON);

        // Then
        assertThat(geoJson1.getId()).isNotEqualTo(geoJson2.getId());
        assertThat(geoJson1.getId()).startsWith("JLGeoJson");
        assertThat(geoJson2.getId()).startsWith("JLGeoJson");
    }

    // === Multiple Data Sources Test ===

    @Test
    void multipleGeoJsonOperations_shouldExecuteAllScripts() throws JLException {
        // Given
        when(mockGeoJsonContent.load(anyString())).thenReturn(VALID_GEOJSON);
        when(mockGeoJsonFile.load(any(File.class))).thenReturn(VALID_GEOJSON);
        when(mockGeoJsonURL.load(anyString())).thenReturn(VALID_GEOJSON);

        // When
        JLGeoJson geoJson1 = geoJsonLayer.addFromContent(VALID_GEOJSON);
        JLGeoJson geoJson2 = geoJsonLayer.addFromFile(new File("test.geojson"));
        JLGeoJson geoJson3 = geoJsonLayer.addFromUrl("https://example.com/data.geojson");

        geoJsonLayer.removeGeoJson(geoJson1.getId());

        // Then
        verify(engine, times(3)).executeScript(argThat(script -> script.contains("L.geoJSON")));
        verify(engine).executeScript(argThat(script -> script.contains("removeLayer")));

        verify(callbackHandler, times(3)).addJLObject(anyString(), any(JLGeoJson.class));
        verify(callbackHandler).remove(JLGeoJson.class, geoJson1.getId());
    }
}