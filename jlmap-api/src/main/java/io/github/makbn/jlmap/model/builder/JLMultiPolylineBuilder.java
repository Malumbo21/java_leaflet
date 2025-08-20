package io.github.makbn.jlmap.model.builder;

import io.github.makbn.jlmap.model.JLLatLng;
import io.github.makbn.jlmap.model.JLMultiPolyline;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

import java.util.ArrayList;
import java.util.List;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class JLMultiPolylineBuilder extends JLObjectBuilder<JLMultiPolyline, JLMultiPolylineBuilder> {

    List<List<double[]>> latlngGroups = new ArrayList<>();

    public JLMultiPolylineBuilder addLine(List<double[]> latlngs) {
        latlngGroups.add(latlngs);
        return this;
    }

    @Override
    protected String getElementVarName() {
        return uuid;
    }

    @Override
    protected String getElementType() {
        return JLMultiPolyline.class.getSimpleName().toLowerCase();
    }

    @Override
    public String buildJsElement() {
        // Convert coordinates to JS format
        StringBuilder coords = new StringBuilder("[");
        for (int i = 0; i < latlngGroups.size(); i++) {
            if (i > 0) coords.append(",");
            coords.append("[");
            List<double[]> line = latlngGroups.get(i);
            for (int j = 0; j < line.size(); j++) {
                if (j > 0) coords.append(",");
                double[] pt = line.get(j);
                coords.append(String.format("[%f,%f]", pt[0], pt[1]));
            }
            coords.append("]");
        }
        coords.append("]");

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
                coords,
                renderOptions(),
                getElementVarName(),
                renderCallbacks());
    }

    @Override
    public JLMultiPolyline buildJLObject() {
        return JLMultiPolyline.builder()
                .id(uuid)
                .options(jlOptions)
                .transport(transporter)
                .vertices(toVertices(latlngGroups))
                .build();
    }


    private JLLatLng[][] toVertices(List<List<double[]>> latlngGroups) {
        JLLatLng[][] result = new JLLatLng[latlngGroups.size()][];
        for (int i = 0; i < latlngGroups.size(); i++) {
            List<double[]> group = latlngGroups.get(i);
            JLLatLng[] vertices = new JLLatLng[group.size()];
            for (int j = 0; j < group.size(); j++) {
                double[] coords = group.get(j);
                vertices[j] = new JLLatLng(coords[0], coords[1]);
            }
            result[i] = vertices;
        }
        return result;
    }

}
