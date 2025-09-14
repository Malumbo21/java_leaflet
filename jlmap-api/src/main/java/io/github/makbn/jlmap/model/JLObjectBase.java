package io.github.makbn.jlmap.model;

import io.github.makbn.jlmap.engine.JLServerToClientTransporter;
import io.github.makbn.jlmap.engine.JLTransportRequest;
import io.github.makbn.jlmap.listener.OnJLActionListener;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;


@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
@FieldDefaults(level = AccessLevel.PROTECTED, makeFinal = true)
public abstract class JLObjectBase<T extends JLObject<T>> implements JLObject<T> {

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

    @Override
    public OnJLActionListener<T> getOnActionListener() {
        return listener;
    }

    @Override
    public void setOnActionListener(OnJLActionListener<T> listener) {
        this.listener = listener;
    }

    @Override
    public T setZIndexOffset(int offset) {
        getTransport().execute(new JLTransportRequest(this,
                String.format("this.%s.setZIndexOffset(%d);", getJLId(), offset)));
        this.zIndexOffset = offset;
        return self();
    }

    @Override
    public T setJLObjectOpacity(double opacity) {
        getTransport().execute(new JLTransportRequest(this,
                String.format("this.%s.setOpacity(%f);", getJLId(), opacity)));
        this.opacity = opacity;
        return self();
    }

}
