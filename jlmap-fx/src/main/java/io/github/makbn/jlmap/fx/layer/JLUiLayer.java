package io.github.makbn.jlmap.fx.layer;

import io.github.makbn.jlmap.JLMapCallbackHandler;
import io.github.makbn.jlmap.engine.JLServerToClientTransporter;
import io.github.makbn.jlmap.engine.JLWebEngine;
import io.github.makbn.jlmap.layer.leaflet.LeafletUILayerInt;
import io.github.makbn.jlmap.listener.JLAction;
import io.github.makbn.jlmap.model.*;
import io.github.makbn.jlmap.model.builder.JLImageOverlayBuilder;
import io.github.makbn.jlmap.model.builder.JLMarkerBuilder;
import io.github.makbn.jlmap.model.builder.JLPopupBuilder;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Represents the UI layer on Leaflet map.
 *
 * @author Matt Akbarian  (@makbn)
 */
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class JLUiLayer extends JLLayer implements LeafletUILayerInt {
    JLServerToClientTransporter<Object> transporter;
    AtomicInteger idGenerator;

    public JLUiLayer(JLWebEngine<Object> engine, JLMapCallbackHandler callbackHandler) {
        super(engine, callbackHandler);
        this.idGenerator = new AtomicInteger();
        this.transporter = () -> transport -> {
            // NO-OP
            return null;
        };
    }

    /**
     * Add a {{@link JLMarker}} to the map with given text as content and {{@link JLLatLng}} as position.
     *
     * @param latLng position on the map.
     * @param text   content of the related popup if available!
     * @return the instance of added {{@link JLMarker}} on the map.
     */
    @Override
    public JLMarker addMarker(JLLatLng latLng, String text, boolean draggable) {
        String elementUniqueName = getElementUniqueName(JLGeoJson.class, idGenerator.incrementAndGet());
        JLMarkerBuilder markerBuilder = new JLMarkerBuilder()
                .setUuid(elementUniqueName)
                .setLat(latLng.getLat())
                .setLng(latLng.getLng())
                .setText(text)
                .setTransporter(transporter)
                .withCallbacks(jlCallbackBuilder -> {
                    jlCallbackBuilder.on(JLAction.MOVE);
                    jlCallbackBuilder.on(JLAction.MOVE_START);
                    jlCallbackBuilder.on(JLAction.MOVE_END);
                    jlCallbackBuilder.on(JLAction.DRAG);
                    jlCallbackBuilder.on(JLAction.DRAG_START);
                    jlCallbackBuilder.on(JLAction.DRAG_END);
                    jlCallbackBuilder.on(JLAction.ADD);
                    jlCallbackBuilder.on(JLAction.REMOVE);
                    jlCallbackBuilder.on(JLAction.CLICK);
                    jlCallbackBuilder.on(JLAction.DOUBLE_CLICK);
                })
                .withOptions(JLOptions.DEFAULT.toBuilder().draggable(draggable).build());

        engine.executeScript(markerBuilder.buildJsElement());
        JLMarker marker = markerBuilder.buildJLObject();
        callbackHandler.addJLObject(elementUniqueName, marker);
        return marker;
    }

    /**
     * Remove a {{@link JLMarker}} from the map.
     *
     * @param id of the marker for removing.
     * @return {{@link Boolean#TRUE}} if removed successfully.
     */
    @Override
    public boolean removeMarker(String id) {
        engine.executeScript(removeLayerWithUUID(id));
        callbackHandler.remove(JLMarker.class, id);
        return true;
    }

    /**
     * Add a {{@link JLPopup}} to the map with given text as content and
     * {@link JLLatLng} as position.
     *
     * @param latLng  position on the map.
     * @param text    content of the popup.
     * @param options see {{@link JLOptions}} for customizing
     * @return the instance of added {{@link JLPopup}} on the map.
     */
    @Override
    public JLPopup addPopup(JLLatLng latLng, String text, JLOptions options) {
        String elementUniqueName = getElementUniqueName(JLGeoJson.class, idGenerator.incrementAndGet());
        JLPopupBuilder popupBuilder = new JLPopupBuilder()
                .setUuid(elementUniqueName)
                .setLat(latLng.getLat())
                .setLng(latLng.getLng())
                .setContent(text)
                .withOptions(options)
                .withCallbacks(jlCallbackBuilder -> {
                    jlCallbackBuilder.on(JLAction.CLICK);
                    jlCallbackBuilder.on(JLAction.DOUBLE_CLICK);
                    jlCallbackBuilder.on(JLAction.ADD);
                    jlCallbackBuilder.on(JLAction.REMOVE);
                })
                .setTransporter(transporter);
        engine.executeScript(popupBuilder.buildJsElement());
        JLPopup popup = popupBuilder.buildJLObject();
        callbackHandler.addJLObject(elementUniqueName, popup);
        return popup;
    }

    /**
     * Add popup with {{@link JLOptions#DEFAULT}} options
     *
     * @see JLUiLayer#addPopup(JLLatLng, String, JLOptions)
     */
    @Override
    public JLPopup addPopup(JLLatLng latLng, String text) {
        return addPopup(latLng, text, JLOptions.DEFAULT);
    }

    /**
     * Remove a {@link JLPopup} from the map.
     *
     * @param id of the marker for removing.
     * @return true if removed successfully.
     */
    @Override
    public boolean removePopup(String id) {
        engine.executeScript(removeLayerWithUUID(id));
        callbackHandler.remove(JLPopup.class, id);
        return true;
    }

    /**
     * Adds an image overlay to the map at the specified bounds with the given image URL and options.
     *
     * @param bounds   the geographical bounds the image is tied to
     * @param imageUrl URL of the image to be used as an overlay
     * @param options  theming options for JLImageOverlay
     * @return the instance of added {@link JLImageOverlay} on the map
     */
    @Override
    public JLImageOverlay addImage(JLBounds bounds, String imageUrl, JLOptions options) {
        String elementUniqueName = getElementUniqueName(JLGeoJson.class, idGenerator.incrementAndGet());
        JLImageOverlayBuilder imageBuilder = new JLImageOverlayBuilder()
                .setUuid(elementUniqueName)
                .setImageUrl(imageUrl)
                .setBounds(List.of(
                        new double[]{bounds.getSouthWest().getLat(), bounds.getSouthWest().getLng()},
                        new double[]{bounds.getNorthEast().getLat(), bounds.getNorthEast().getLng()}
                ))
                .setTransporter(transporter)
                .withCallbacks(jlCallbackBuilder -> {
                    jlCallbackBuilder.on(JLAction.CLICK);
                    jlCallbackBuilder.on(JLAction.DOUBLE_CLICK);
                    jlCallbackBuilder.on(JLAction.ADD);
                    jlCallbackBuilder.on(JLAction.REMOVE);
                })
                .withOptions(options);
        engine.executeScript(imageBuilder.buildJsElement());
        JLImageOverlay overlay = imageBuilder.buildJLObject();
        callbackHandler.addJLObject(elementUniqueName, overlay);
        return overlay;
    }
}
