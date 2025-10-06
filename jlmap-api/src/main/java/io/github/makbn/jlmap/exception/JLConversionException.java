package io.github.makbn.jlmap.exception;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

/**
 * JLMap exception class for converting js results
 *
 * @author Matt Akbarian  (@makbn)
 */
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class JLConversionException extends JLException {
    transient Object rawValue;

    public JLConversionException(Throwable cause, Object rawValue) {
        super(cause);
        this.rawValue = rawValue;
    }
}
