package io.github.makbn.jlmap.vaadin;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.ClientCallable;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.dependency.JavaScript;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.dependency.NpmPackage;
import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.orderedlayout.BoxSizing;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.page.PendingJavaScriptResult;
import io.github.makbn.jlmap.JLMapCallbackHandler;
import io.github.makbn.jlmap.JLMapController;
import io.github.makbn.jlmap.engine.JLWebEngine;
import io.github.makbn.jlmap.layer.leaflet.LeafletLayer;
import io.github.makbn.jlmap.listener.OnJLMapViewListener;
import io.github.makbn.jlmap.map.JLMapProvider;
import io.github.makbn.jlmap.model.JLLatLng;
import io.github.makbn.jlmap.model.JLMapOption;
import io.github.makbn.jlmap.vaadin.bridge.JLVaadinClientToServerTransporter;
import io.github.makbn.jlmap.vaadin.engine.JLVaadinEngine;
import io.github.makbn.jlmap.vaadin.layer.JLVaadinControlLayer;
import io.github.makbn.jlmap.vaadin.layer.JLVaadinGeoJsonLayer;
import io.github.makbn.jlmap.vaadin.layer.JLVaadinUiLayer;
import io.github.makbn.jlmap.vaadin.layer.JLVaadinVectorLayer;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Objects;
import java.util.Set;

/**
 * A Vaadin component that displays a Leaflet map.
 * This component implements the JLMapController interface to provide
 * a consistent API across different UI frameworks.
 *
 * @author Matt Akbarian  (@makbn)
 */
@Slf4j
@Tag("jl-map-view")
@JsModule("leaflet/dist/leaflet.js")
@NpmPackage(value = "leaflet", version = "1.9.4")
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@StyleSheet("https://unpkg.com/leaflet@1.9.4/dist/leaflet.css")
@NpmPackage(value = "@maptiler/leaflet-maptilersdk", version = "4.1.0")
@JavaScript("https://unpkg.com/leaflet-providers@latest/leaflet-providers.js")
public class JLMapView extends VerticalLayout implements JLMapController<PendingJavaScriptResult> {
    transient JLMapOption mapOption;
    transient JLMapCallbackHandler jlMapCallbackHandler;
    transient JLWebEngine<PendingJavaScriptResult> jlWebEngine;
    @Getter
    transient HashMap<Class<? extends LeafletLayer>, LeafletLayer> layers;
    @NonFinal
    transient boolean controllerAdded = false;
    @NonFinal
    @Nullable
    transient OnJLMapViewListener mapListener;

    /**
     * Creates a new JLMapView with the specified map type, starting coordinates, and zoom controller visibility.
     *
     * @param jlMapProvider            the type of map to display
     * @param startCoordinate    the initial center coordinates of the map
     * @param showZoomController whether to show the zoom controller
     */
    @Builder
    public JLMapView(@NonNull JLMapProvider jlMapProvider,
                     @NonNull JLLatLng startCoordinate, boolean showZoomController) {
        super();
        setSizeFull();
        setMinHeight("100%");
        setMargin(false);
        setSpacing(false);
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);
        setBoxSizing(BoxSizing.CONTENT_BOX);
        this.mapOption = JLMapOption.builder()
                .startCoordinate(startCoordinate)
                .jlMapProvider(jlMapProvider)
                .additionalParameter(Set.of(new JLMapOption.Parameter("zoomControl",
                        Objects.toString(showZoomController))))
                .build();
        this.jlWebEngine = new JLVaadinEngine(this::getElement);
        this.jlMapCallbackHandler = new JLMapCallbackHandler(mapListener);
        this.layers = new HashMap<>();
    }

    /**
     * Initializes the map when the component is attached to the DOM.
     *
     * @param attachEvent the attach event
     */
    @Override
    protected void onAttach(AttachEvent attachEvent) {
        super.onAttach(attachEvent);
        log.debug("onAttach: {}", attachEvent);
        getElement().executeJs(generateInitializeFunctionCall());
        initializeLayers();
        addControllerToDocument();

        if (mapListener != null) {
            mapListener.mapLoadedSuccessfully(this);
        }
    }

    /**
     * Generates the JavaScript function call to initialize the map.
     *
     * @return the JavaScript initialization string
     */
    @SuppressWarnings("all")
    private String generateInitializeFunctionCall() {
        String call = """
                this.jlMapElement = document.querySelector('jl-map-view');
                this.map = L.map(this.jlMapElement, {zoomControl: %b}).setView([%s, %s], %d);
                
                L.tileLayer('%s')
                .addTo(this.map);
                """;

        return call.formatted(mapOption.zoomControlEnabled(),
                mapOption.getStartCoordinate().getLat(),
                mapOption.getStartCoordinate().getLng(),
                mapOption.getInitialZoom(),
                mapOption.getJlMapProvider().getMapProviderAddress());
    }

    /**
     * Initializes the map layers.
     */
    private void initializeLayers() {
        layers.clear();

        layers.put(JLVaadinVectorLayer.class, new JLVaadinVectorLayer(jlWebEngine, jlMapCallbackHandler));
        layers.put(JLVaadinUiLayer.class, new JLVaadinUiLayer(jlWebEngine, jlMapCallbackHandler));
        layers.put(JLVaadinControlLayer.class, new JLVaadinControlLayer(jlWebEngine, jlMapCallbackHandler));
        layers.put(JLVaadinGeoJsonLayer.class, new JLVaadinGeoJsonLayer(jlWebEngine, jlMapCallbackHandler));
    }

    /**
     * Called when the map is loaded successfully from JavaScript.
     * Handles events from the client side.
     *
     * @param function         the function name
     * @param jlType           the JL type
     * @param uuid             the unique identifier
     * @param additionalParam1 additional parameter 1
     * @param additionalParam2 additional parameter 2
     * @param additionalParam3 additional parameter 3
     */
    @ClientCallable
    @SuppressWarnings("unused")
    public void eventHandler(String function, String jlType, String uuid, String additionalParam1,
                             String additionalParam2, String additionalParam3) {
        jlMapCallbackHandler.functionCalled(this, String.valueOf(function), jlType, uuid, additionalParam1, additionalParam2, additionalParam3);
    }

    /**
     * Bridge method called from JavaScript to invoke Java methods on registered objects.
     * This enables the JavaScript-to-Java bridge functionality.
     *
     * @param callId     unique identifier for this call
     * @param objectId   the ID of the object to call
     * @param methodName the method to invoke
     * @param argsJson   JSON-encoded arguments
     */
    @ClientCallable
    @SuppressWarnings("unused")
    public String jlObjectBridgeCall(String callId, String objectId, String methodName, String argsJson) {
        JLVaadinGeoJsonLayer geoJsonLayer = (JLVaadinGeoJsonLayer) layers.get(JLVaadinGeoJsonLayer.class);
        if (geoJsonLayer != null && geoJsonLayer.getClientToServer() != null) {
            return ((JLVaadinClientToServerTransporter) geoJsonLayer.getClientToServer())
                    .jlObjectBridgeCall(callId, objectId, methodName, argsJson);
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public JLWebEngine<PendingJavaScriptResult> getJLEngine() {
        return jlWebEngine;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addControllerToDocument() {
        if (!controllerAdded) {
            jlWebEngine.executeScript("window.jlController = this;");
            //language=JavaScript
            jlWebEngine.executeScript("""
                    if (typeof Event === 'function') {
                      window.dispatchEvent(new Event('resize'));
                    } else {
                      // fallback for older browsers
                      var evt = document.createEvent('UIEvents');
                      evt.initUIEvent('resize', true, false, window, 0);
                      window.dispatchEvent(evt);
                    }
                    """);
            controllerAdded = true;
        }
    }

    /**
     * Sets the listener for map view events.
     *
     * @param listener the listener
     */
    public void setMapViewListener(OnJLMapViewListener listener) {
        this.mapListener = listener;
    }

    /**
     * Gets the GeoJson layer for this map view.
     *
     * @return JLVaadinGeoJsonLayer
     */
    @Override
    public JLVaadinGeoJsonLayer getGeoJsonLayer() {
        return (JLVaadinGeoJsonLayer) layers.get(JLVaadinGeoJsonLayer.class);
    }
}