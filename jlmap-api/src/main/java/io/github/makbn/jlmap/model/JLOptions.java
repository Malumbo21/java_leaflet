package io.github.makbn.jlmap.model;

import lombok.*;
import lombok.experimental.FieldDefaults;

/**
 * Optional value for theming objects inside the map!
 * Note that all options are not available for all objects!
 * Read more at Leaflet Official Docs.
 * @author Mehdi Akbarian Rastaghi (@makbn)
 */
@Getter
@Setter
@AllArgsConstructor
@Builder(toBuilder = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class JLOptions {

    /** Default value for theming options. */
    public static final JLOptions DEFAULT = JLOptions.builder().build();

    /** Stroke color. Default is {{@link JLColor#BLUE}} */
    @Builder.Default
    JLColor color = JLColor.BLUE;

    /** Fill color. Default is {{@link JLColor#BLUE}} */
    @Builder.Default
    JLColor fillColor = JLColor.BLUE;

    /** Stroke width in pixels. Default is 3 */
    @Builder.Default
    int weight = 3;

    /**
     * Whether to draw stroke along the path. Set it to false for disabling
     * borders on polygons or circles.
     */
    @Builder.Default
    boolean stroke = true;

    /** Whether to fill the path with color. Set it to false fo disabling
     * filling on polygons or circles.
     */
    @Builder.Default
    boolean fill = true;

    /** Stroke opacity */
    @Builder.Default
    double opacity = 1.0;

    /** Fill opacity. */
    @Builder.Default
    double fillOpacity = 0.2;

    /** How much to simplify the polyline on each zoom level.
     * greater value means better performance and smoother
     * look, and smaller value means more accurate representation.
     */
    @Builder.Default
    double smoothFactor = 1.0;

    /** Controls the presence of a close button in the popup.
     */
    @Builder.Default
    boolean closeButton = true;

    /** Set it to false if you want to override the default behavior
     * of the popup closing when another popup is opened.
     */
    @Builder.Default
    boolean autoClose = true;

    /** Whether the marker is draggable with mouse/touch or not.
     */
    @Builder.Default
    boolean draggable = false;

    JLObject<?> parent;

}
