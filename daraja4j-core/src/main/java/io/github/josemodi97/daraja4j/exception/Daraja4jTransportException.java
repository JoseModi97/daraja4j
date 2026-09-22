package io.github.josemodi97.daraja4j.exception;

import java.io.IOException;

/**
 * Wraps a lower-level {@link IOException} raised while talking to Daraja over
 * HTTP (connect timeout, DNS failure, TLS error, etc.).
 */
public class Daraja4jTransportException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public Daraja4jTransportException(String message, IOException cause) {
        super(message, cause);
    }
}
