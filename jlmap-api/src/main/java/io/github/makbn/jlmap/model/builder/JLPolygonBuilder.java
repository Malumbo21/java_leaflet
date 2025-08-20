package io.github.makbn.jlmap.model.builder;

import io.github.makbn.jlmap.model.JLLatLng;
import io.github.makbn.jlmap.model.JLPolygon;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Matt Akbarian  (@makbn)
 */
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class JLPolygonBuilder extends JLObjectBuilder<JLPolygon, JLPolygonBuilder> {

    List<List<double[]>> latlngGroups = new ArrayList<>();

    public JLPolygonBuilder addLatLngGroup(List<double[]> group) {
        this.latlngGroups.add(group);
        return this;
    }

    public JLPolygonBuilder addLatLng(double lat, double lng) {
        if (latlngGroups.isEmpty()) {
            latlngGroups.add(new ArrayList<>());
        }
        latlngGroups.get(latlngGroups.size() - 1).add(new double[]{lat, lng});
        return this;
    }

    @Override
    protected String getElementVarName() {
        return uuid;
    }

    @Override
    protected String getElementType() {
        return JLPolygon.class.getSimpleName().toLowerCase();
    }

    @Override
    public String buildJsElement() {
        String latlngsJs = latlngGroups.stream()
                .map(group -> group.stream()
                        .map(coord -> String.format("[%f, %f]", coord[0], coord[1]))
                        .collect(Collectors.joining(",", "[", "]"))
                ).collect(Collectors.joining(",", "[", "]"));

        return String.format("""
                        let %1$s = L.polygon(%2$s, { %3$s });
                        this.%1$s = %1$s;
                        %1$s.uuid = '%4$s';
                        // callback start
                        %5$s
                        // callback end
                        %1$s.addTo(this.map);
                        """,
                getElementVarName(),
                latlngsJs,
                renderOptions(),
                getElementVarName(),
                renderCallbacks());
    }

    @Override
    public JLPolygon buildJLObject() {
        return JLPolygon.builder()
                .id(uuid)
                .options(jlOptions)
                .transport(transporter)
                .vertices(toVertices(latlngGroups))
                .build();
    }

    private static JLLatLng[][][] toVertices(List<List<double[]>> latlngGroups) {
        if (latlngGroups == null) {
            return new JLLatLng[0][][];
        }

        return latlngGroups.stream()
                .map(group -> group.stream()
                        .map(coord -> new JLLatLng(coord[0], coord[1]))
                        .toArray(JLLatLng[]::new)
                )
                .map(ring -> new JLLatLng[][]{ring}) // wrap each ring as a polygon
                .toArray(JLLatLng[][][]::new);
    }

}
