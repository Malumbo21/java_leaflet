package io.github.makbn.jlmap.model.builder;

import io.github.makbn.jlmap.model.JLLatLng;
import io.github.makbn.jlmap.model.JLPolyline;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class JLPolylineBuilder extends JLObjectBuilder<JLPolyline, JLPolylineBuilder> {
    List<double[]> latlngs = new ArrayList<>();

    public JLPolylineBuilder addLatLng(double lat, double lng) {
        latlngs.add(new double[]{lat, lng});
        return this;
    }

    public JLPolylineBuilder addLatLngs(List<double[]> points) {
        latlngs.addAll(points);
        return this;
    }


    @Override
    protected String getElementVarName() {
        return uuid;
    }

    @Override
    protected String getElementType() {
        return JLPolyline.class.getTypeName().toLowerCase();
    }

    @Override
    public String buildJsElement() {
        String latlngArray = latlngs.stream()
                .map(coord -> "[" + coord[0] + "," + coord[1] + "]")
                .collect(Collectors.joining(",", "[", "]"));

        return String.format("""
                        let %1$s = L.polyline(%2$s, { %3$s });
                        this.%1$s = %1$s;
                        %1$s.uuid = '%4$s';
                        // callback start
                        %5$s
                        // callback end
                        %1$s.addTo(this.map);
                        """,
                getElementVarName(),
                latlngArray,
                renderOptions(),
                getElementVarName(),
                renderCallbacks());
    }

    @Override
    public JLPolyline buildJLObject() {
        return JLPolyline.builder()
                .id(uuid)
                .options(jlOptions)
                .transport(transporter)
                .vertices(toVertices(latlngs))
                .build();
    }

    private static JLLatLng[] toVertices(List<double[]> latlngs) {
        if (latlngs == null) {
            return new JLLatLng[0];
        }

        JLLatLng[] vertices = new JLLatLng[latlngs.size()];
        for (int i = 0; i < latlngs.size(); i++) {
            double[] pair = latlngs.get(i);
            if (pair == null || pair.length < 2) {
                throw new IllegalArgumentException("Each element must be a double array of length 2 [lat, lng]");
            }
            vertices[i] = new JLLatLng(pair[0], pair[1]);
        }
        return vertices;
    }
}
