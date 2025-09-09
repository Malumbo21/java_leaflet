package io.github.makbn.jlmap.vaadin.test.layer;

import com.vaadin.flow.component.page.PendingJavaScriptResult;
import io.github.makbn.jlmap.JLMapCallbackHandler;
import io.github.makbn.jlmap.engine.JLWebEngine;
import io.github.makbn.jlmap.exception.JLException;
import io.github.makbn.jlmap.geojson.JLGeoJsonContent;
import io.github.makbn.jlmap.geojson.JLGeoJsonFile;
import io.github.makbn.jlmap.geojson.JLGeoJsonURL;
import io.github.makbn.jlmap.model.JLGeoJson;
import io.github.makbn.jlmap.vaadin.layer.JLVaadinGeoJsonLayer;
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
class JLVaadinGeoJsonLayerTest {

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
    private JLWebEngine<PendingJavaScriptResult> engine;

    @Mock
    private JLMapCallbackHandler callbackHandler;

    @Mock
    private PendingJavaScriptResult mockJavaScriptResult;

    @Mock
    private JLGeoJsonFile mockGeoJsonFile;

    @Mock
    private JLGeoJsonURL mockGeoJsonURL;

    @Mock
    private JLGeoJsonContent mockGeoJsonContent;

    private JLVaadinGeoJsonLayer geoJsonLayer;

    @BeforeEach
    void setUp() {
        geoJsonLayer = new JLVaadinGeoJsonLayer(engine, callbackHandler);

        // Use reflection to inject mocks for testing
        try {
            var fromFileField = JLVaadinGeoJsonLayer.class.getDeclaredField("fromFile");
            fromFileField.setAccessible(true);
            fromFileField.set(geoJsonLayer, mockGeoJsonFile);

            var fromUrlField = JLVaadinGeoJsonLayer.class.getDeclaredField("fromUrl");
            fromUrlField.setAccessible(true);
            fromUrlField.set(geoJsonLayer, mockGeoJsonURL);

            var fromContentField = JLVaadinGeoJsonLayer.class.getDeclaredField("fromContent");
            fromContentField.setAccessible(true);
            fromContentField.set(geoJsonLayer, mockGeoJsonContent);
        } catch (Exception e) {
            throw new RuntimeException("Failed to set up mocks", e);
        }
    }

    @Test
    void constructor_withNullEngine_shouldAcceptNullEngine() {
        JLVaadinGeoJsonLayer layer = new JLVaadinGeoJsonLayer(null, callbackHandler);
        assertThat(layer).isNotNull();
    }

    @Test
    void constructor_withNullCallbackHandler_shouldAcceptNullHandler() {
        JLVaadinGeoJsonLayer layer = new JLVaadinGeoJsonLayer(engine, null);
        assertThat(layer).isNotNull();
    }

    @Test
    void geoJsonLayer_shouldExtendJLLayer() {
        assertThat(geoJsonLayer).isInstanceOf(io.github.makbn.jlmap.vaadin.layer.JLVaadinLayer.class);
    }

    @Test
    void addFromFile_withValidFile_shouldLoadFileAndExecuteScript() throws JLException {
        when(engine.executeScript(anyString())).thenReturn(mockJavaScriptResult);
        File testFile = new File("test.geojson");
        when(mockGeoJsonFile.load(testFile)).thenReturn(VALID_GEOJSON);

        JLGeoJson result = geoJsonLayer.addFromFile(testFile);

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
        File testFile = new File("invalid.geojson");
        JLException expectedException = new JLException("File not found");
        when(mockGeoJsonFile.load(testFile)).thenThrow(expectedException);

        assertThatThrownBy(() -> geoJsonLayer.addFromFile(testFile))
                .isInstanceOf(JLException.class)
                .hasMessage("File not found");

        verify(mockGeoJsonFile).load(testFile);
        verifyNoInteractions(engine);
        verifyNoInteractions(callbackHandler);
    }

    @Test
    void addFromFile_withNullFile_shouldThrowNullPointerException() {
        assertThatThrownBy(() -> geoJsonLayer.addFromFile(null))
                .isInstanceOf(NullPointerException.class);

        verifyNoInteractions(mockGeoJsonFile);
        verifyNoInteractions(engine);
        verifyNoInteractions(callbackHandler);
    }

    @Test
    void addFromUrl_withValidUrl_shouldLoadUrlAndExecuteScript() throws JLException {
        when(engine.executeScript(anyString())).thenReturn(mockJavaScriptResult);
        String testUrl = "https://example.com/data.geojson";
        String simpleGeoJson = """
                {
                    "type": "Point",
                    "coordinates": [13.4050, 52.5200]
                }""";
        when(mockGeoJsonURL.load(testUrl)).thenReturn(simpleGeoJson);

        JLGeoJson result = geoJsonLayer.addFromUrl(testUrl);

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
        String testUrl = "https://invalid-url.com/data.geojson";
        JLException expectedException = new JLException("URL not accessible");
        when(mockGeoJsonURL.load(testUrl)).thenThrow(expectedException);

        assertThatThrownBy(() -> geoJsonLayer.addFromUrl(testUrl))
                .isInstanceOf(JLException.class)
                .hasMessage("URL not accessible");

        verify(mockGeoJsonURL).load(testUrl);
        verifyNoInteractions(engine);
        verifyNoInteractions(callbackHandler);
    }

    @Test
    void addFromUrl_withNullUrl_shouldThrowNullPointerException() {
        assertThatThrownBy(() -> geoJsonLayer.addFromUrl(null))
                .isInstanceOf(NullPointerException.class);

        verifyNoInteractions(mockGeoJsonURL);
        verifyNoInteractions(engine);
        verifyNoInteractions(callbackHandler);
    }

    @Test
    void addFromContent_withValidContent_shouldLoadContentAndExecuteScript() throws JLException {
        when(engine.executeScript(anyString())).thenReturn(mockJavaScriptResult);
        String content = VALID_GEOJSON;
        when(mockGeoJsonContent.load(content)).thenReturn(content);

        JLGeoJson result = geoJsonLayer.addFromContent(content);

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
        String content = "invalid json";
        JLException expectedException = new JLException("Invalid GeoJSON");
        when(mockGeoJsonContent.load(content)).thenThrow(expectedException);

        assertThatThrownBy(() -> geoJsonLayer.addFromContent(content))
                .isInstanceOf(JLException.class)
                .hasMessage("Invalid GeoJSON");

        verify(mockGeoJsonContent).load(content);
        verifyNoInteractions(engine);
        verifyNoInteractions(callbackHandler);
    }

    @Test
    void addFromContent_withNullContent_shouldThrowNullPointerException() {
        assertThatThrownBy(() -> geoJsonLayer.addFromContent(null))
                .isInstanceOf(NullPointerException.class);

        verifyNoInteractions(mockGeoJsonContent);
        verifyNoInteractions(engine);
        verifyNoInteractions(callbackHandler);
    }

    @Test
    void removeGeoJson_shouldExecuteRemoveScriptAndRemoveFromCallback() {
        when(engine.executeScript(anyString())).thenReturn(mockJavaScriptResult);
        String geoJsonId = "testGeoJsonId";

        boolean result = geoJsonLayer.removeGeoJson(geoJsonId);

        verify(engine).executeScript("this.map.removeLayer(this.testGeoJsonId)");
        verify(callbackHandler).remove(JLGeoJson.class, geoJsonId);
        assertThat(result).isTrue();
    }

    @Test
    void removeGeoJson_withNullId_shouldThrowNullPointerException() {
        assertThatThrownBy(() -> geoJsonLayer.removeGeoJson(null))
                .isInstanceOf(NullPointerException.class);

        verifyNoInteractions(engine);
        verifyNoInteractions(callbackHandler);
    }

    @Test
    void geoJsonLayer_shouldGenerateUniqueIds() throws JLException {
        when(engine.executeScript(anyString())).thenReturn(mockJavaScriptResult);
        when(mockGeoJsonContent.load(anyString())).thenReturn(VALID_GEOJSON);

        JLGeoJson geoJson1 = geoJsonLayer.addFromContent(VALID_GEOJSON);
        JLGeoJson geoJson2 = geoJsonLayer.addFromContent(VALID_GEOJSON);

        assertThat(geoJson1.getId()).isNotEqualTo(geoJson2.getId());
        assertThat(geoJson1.getId()).startsWith("JLGeoJson");
        assertThat(geoJson2.getId()).startsWith("JLGeoJson");
    }

    @Test
    void multipleGeoJsonOperations_shouldExecuteAllScripts() throws JLException {
        when(engine.executeScript(anyString())).thenReturn(mockJavaScriptResult);
        when(mockGeoJsonContent.load(anyString())).thenReturn(VALID_GEOJSON);
        when(mockGeoJsonFile.load(any(File.class))).thenReturn(VALID_GEOJSON);
        when(mockGeoJsonURL.load(anyString())).thenReturn(VALID_GEOJSON);

        JLGeoJson geoJson1 = geoJsonLayer.addFromContent(VALID_GEOJSON);
        JLGeoJson geoJson2 = geoJsonLayer.addFromFile(new File("test.geojson"));
        JLGeoJson geoJson3 = geoJsonLayer.addFromUrl("https://example.com/data.geojson");

        geoJsonLayer.removeGeoJson(geoJson1.getId());

        verify(engine, times(3)).executeScript(argThat(script -> script.contains("L.geoJSON")));
        verify(engine).executeScript(argThat(script -> script.contains("removeLayer")));

        verify(callbackHandler, times(3)).addJLObject(anyString(), any(JLGeoJson.class));
        verify(callbackHandler).remove(JLGeoJson.class, geoJson1.getId());
    }
}