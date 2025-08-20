package io.github.makbn.jlmap.exception;

import lombok.Builder;

/**
 * @author Matt Akbarian  (@makbn)
 */
public class JLGeoJsonParserException extends JLException {

    @Builder
    public JLGeoJsonParserException(String message) {
        super(message);
    }
}
