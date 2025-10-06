package io.github.makbn.jlmap.model;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

/**
 * @author Matt Akbarian  (@makbn)
 */
@Getter
@Builder
@AllArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class JLIcon {
    String iconUrl;
    String shadowUrl;
    JLPoint iconSize;
    JLPoint iconAnchor;
    JLPoint popupAnchor;
    JLPoint shadowSize;
    JLPoint shadowAnchor;


    @Override
    public String toString() {
        return "L.icon({" +
                "iconUrl: '" + getIconUrl() + '\'' +
                (getShadowUrl() != null ? ", shadowUrl: '" + getShadowUrl() + '\'' : "") +
                ", iconSize: " + getIconSize() +
                ", iconAnchor: " + getIconAnchor() +
                ", popupAnchor: " + getPopupAnchor() +
                ", shadowSize: " + getShadowSize() +
                ", shadowAnchor: " + getShadowAnchor() +
                "})";
    }
}
