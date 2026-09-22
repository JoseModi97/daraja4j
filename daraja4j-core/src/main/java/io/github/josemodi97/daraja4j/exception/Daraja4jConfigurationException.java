package io.github.josemodi97.daraja4j.exception;

/**
 * Thrown when a {@link io.github.josemodi97.daraja4j.Daraja4jConfig} is
 * missing a required setting (consumer key, consumer secret, environment, etc.).
 */
public class Daraja4jConfigurationException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public Daraja4jConfigurationException(String message) {
        super(message);
    }
}
