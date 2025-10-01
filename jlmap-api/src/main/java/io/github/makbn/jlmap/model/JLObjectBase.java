package io.github.makbn.jlmap.model;

import io.github.makbn.jlmap.element.menu.JLContextMenu;
import io.github.makbn.jlmap.element.menu.JLHasContextMenu;
import io.github.makbn.jlmap.engine.JLServerToClientTransporter;
import io.github.makbn.jlmap.engine.JLTransportRequest;
import io.github.makbn.jlmap.listener.OnJLActionListener;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;


@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
@FieldDefaults(level = AccessLevel.PROTECTED, makeFinal = true)
public abstract class JLObjectBase<T extends JLObject<T>> implements JLObject<T>, JLHasContextMenu<T> {

    /**
     * id of object! this is an internal id for JLMap Application and not
     * related to Leaflet!
     */
    @Getter
    String jLId;

    @Getter
    JLServerToClientTransporter<?> transport;

    @NonFinal
    OnJLActionListener<T> listener;

    @Getter
    @Setter
    @NonFinal
    JLPopup popup;

    /**
     * By default, marker images zIndex is set automatically based on its latitude.
     * Use this option if you want to put the marker on top of all others (or below),
     * specifying a high value like 1000 (or high negative value, respectively).
     */
    @NonFinal
    int zIndexOffset = 0;
    /**
     * The opacity of the marker.
     */
    @NonFinal
    double opacity = 1.0;

    /**
     * Context menu for this object (lazily initialized).
     */
    @NonFinal
    JLContextMenu<T> contextMenu;

    /**
     * Whether the context menu is enabled for this object.
     */
    @NonFinal
    boolean contextMenuEnabled = true;

    @Override
    public OnJLActionListener<T> getOnActionListener() {
        return listener;
    }

    @Override
    public void setOnActionListener(OnJLActionListener<T> listener) {
        this.listener = listener;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public T setZIndexOffset(int offset) {
        getTransport().execute(JLTransportRequest.voidCall(this, "setZIndexOffset", offset));
        this.zIndexOffset = offset;
        return self();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public T setJLObjectOpacity(double opacity) {
        getTransport().execute(JLTransportRequest.voidCall(this, "setOpacity", opacity));
        this.opacity = opacity;
        return self();
    }

    /**
     * Removes the layer from the map it is currently active on.
     *
     * @return removed object
     * @see <a href="https://leafletjs.com/reference.html#circle-remove">Leaflet docs</a>
     */
    public T remove() {
        getTransport().execute(JLTransportRequest.voidCall(self(), "remove"));
        return self();
    }

    /**
     * Redraws the layer. Sometimes useful after you changed the coordinates that the path uses.
     *
     * @return the object itself for method chaining
     * @see <a href="https://leafletjs.com/reference.html#path-redraw">Leaflet docs</a>
     */
    public T redraw() {
        getTransport().execute(JLTransportRequest.voidCall(this, "redraw"));
        return self();
    }

    /**
     * Changes the appearance of a Path based on the options in the given object.
     *
     * @param style new style options for the path
     * @return the object itself for method chaining
     * @see <a href="https://leafletjs.com/reference.html#path-setstyle">Leaflet docs</a>
     */
    public T setStyle(@NonNull JLOptions style) {
        getTransport().execute(JLTransportRequest.voidCall(this, "setStyle", style.toString()));
        return self();
    }

    /**
     * Brings the layer to the top of all path layers.
     *
     * @return the object itself for method chaining
     * @see <a href="https://leafletjs.com/reference.html#path-bringtotop">Leaflet docs</a>
     */
    public T bringToFront() {
        getTransport().execute(JLTransportRequest.voidCall(this, "bringToFront"));
        return self();
    }

    /**
     * Brings the layer to the bottom of all path layers.
     *
     * @return the object itself for method chaining
     * @see <a href="https://leafletjs.com/reference.html#path-bringtoback">Leaflet docs</a>
     */
    public T bringToBack() {
        getTransport().execute(JLTransportRequest.voidCall(this, "bringToBack"));
        return self();
    }

    /**
     * Returns the attribution text of the layer.
     *
     * @return the attribution text of the layer
     * @see <a href="https://leafletjs.com/reference.html#layer-getattribution">Leaflet docs</a>
     */
    public CompletableFuture<String> getAttribution() {
        return getTransport().execute(JLTransportRequest.returnableCall(this, "getAttribution", String.class));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Nullable
    public synchronized JLContextMenu<T> getContextMenu() {
        return contextMenu;
    }

    @Override
    public synchronized void setContextMenu(@NonNull JLContextMenu<T> contextMenu) {
        this.contextMenu = contextMenu;
    }

    @NonNull
    @Override
    public synchronized JLContextMenu<T> addContextMenu() {
        this.contextMenu = new JLContextMenu<>(self());
        return getContextMenu();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean hasContextMenu() {
        return contextMenu != null && contextMenu.hasVisibleItems();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isContextMenuEnabled() {
        return contextMenuEnabled;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setContextMenuEnabled(boolean enabled) {
        this.contextMenuEnabled = enabled;
    }
}
