package io.github.makbn.jlmap.vaadin.layer;

import com.vaadin.flow.component.page.PendingJavaScriptResult;
import io.github.makbn.jlmap.JLMapCallbackHandler;
import io.github.makbn.jlmap.engine.JLWebEngine;
import io.github.makbn.jlmap.layer.leaflet.LeafletUILayerInt;
import io.github.makbn.jlmap.listener.JLAction;
import io.github.makbn.jlmap.model.*;
import io.github.makbn.jlmap.model.builder.JLImageOverlayBuilder;
import io.github.makbn.jlmap.model.builder.JLMarkerBuilder;
import io.github.makbn.jlmap.model.builder.JLPopupBuilder;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Represents the UI layer on Leaflet map.
 *
 * @author Matt Akbarian  (@makbn)
 */
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class JLVaadinUiLayer extends JLVaadinLayer implements LeafletUILayerInt {
    AtomicInteger idGenerator;

    public JLVaadinUiLayer(JLWebEngine<PendingJavaScriptResult> engine, JLMapCallbackHandler callbackHandler) {
        super(engine, callbackHandler);
        this.idGenerator = new AtomicInteger();
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
        String elementUniqueName = getElementUniqueName(JLMarker.class, idGenerator.incrementAndGet());

        var markerBuilder = new JLMarkerBuilder()
                .setUuid(elementUniqueName)
                .setLat(latLng.getLat())
                .setLng(latLng.getLng())
                .setText(text)
                .setTransporter(getTransporter())
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
        var marker = markerBuilder.buildJLObject();
        callbackHandler.addJLObject(elementUniqueName, marker);
        if (text != null && !text.trim().isEmpty()) {
            var attachedPopup = addPopup(latLng, text, JLOptions.DEFAULT.toBuilder().parent(marker).build());
            marker.setPopup(attachedPopup);
        }
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
        try {
            engine.executeScript(removeLayerWithUUID(id));
            callbackHandler.remove(JLMarker.class, id);
        } catch (RuntimeException e) {
            log.error(e.getMessage(), e);
            return false;
        }
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
        String elementUniqueName = getElementUniqueName(JLPopup.class, idGenerator.incrementAndGet());

        JLPopupBuilder popupBuilder = new JLPopupBuilder()
                .setUuid(elementUniqueName)
                .setContent(text)
                .setLat(latLng.getLat())
                .setLng(latLng.getLng())
                .setTransporter(getTransporter())
                .withCallbacks(jlCallbackBuilder -> {
                    jlCallbackBuilder.on(JLAction.MOVE);
                    jlCallbackBuilder.on(JLAction.ADD);
                    jlCallbackBuilder.on(JLAction.REMOVE);
                }).withOptions(options);

        engine.executeScript(popupBuilder.buildJsElement());
        var popup = popupBuilder.buildJLObject();
        callbackHandler.addJLObject(elementUniqueName, popup);
        return popup;
    }

    /**
     * Add popup with {{@link JLOptions#DEFAULT}} options
     *
     * @see JLVaadinUiLayer#addPopup(JLLatLng, String, JLOptions)
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
        try {
            engine.executeScript(removeLayerWithUUID(id));
            callbackHandler.remove(JLPopup.class, id);
        } catch (RuntimeException e) {
            log.error(e.getMessage(), e);
            return false;
        }
        return true;
    }

    @Override
    public JLImageOverlay addImage(JLBounds bounds, String imageUrl, JLOptions options) {
        String elementUniqueName = getElementUniqueName(JLPopup.class, idGenerator.incrementAndGet());

        JLImageOverlayBuilder imageBuilder = new JLImageOverlayBuilder()
                .setUuid(elementUniqueName)
                .setImageUrl(imageUrl)
                .setBounds(List.of(new double[]{bounds.getSouthWest().getLat(), bounds.getSouthWest().getLng()},
                        new double[]{bounds.getNorthEast().getLat(), bounds.getNorthEast().getLng()}))
                .setTransporter(getTransporter())
                .withOptions(options)
                .withCallbacks(jlCallbackBuilder -> {

                });

        engine.executeScript(imageBuilder.buildJsElement());
        var imageOverlay = imageBuilder.buildJLObject();
        callbackHandler.addJLObject(elementUniqueName, imageOverlay);
        return imageOverlay;
    }
}
