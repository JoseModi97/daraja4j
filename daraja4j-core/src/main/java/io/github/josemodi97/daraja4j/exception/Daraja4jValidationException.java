package io.github.josemodi97.daraja4j.exception;

/**
 * Thrown when a request model (STK push, B2C, reversal, etc.) is missing a
 * field required for that particular Daraja operation.
 */
public class Daraja4jValidationException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public Daraja4jValidationException(String message) {
        super(message);
    }
}
