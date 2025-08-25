package io.github.makbn.jlmap.model;

import lombok.*;
import lombok.experimental.FieldDefaults;

/**
 * Represents a point with x and y coordinates in pixels.
 *
 * @author Matt Akbarian  (@makbn)
 */
@Getter
@Setter
@Builder
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class JLPoint {
    double x;
    double y;

    @Override
    public String toString() {
        return '[' + getX() + ", " + getY() + ']';
    }
}
