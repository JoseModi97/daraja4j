package io.github.josemodi97.daraja4j.model;

import io.github.josemodi97.daraja4j.exception.Daraja4jValidationException;

/** Shared validation helpers for the request model classes in this package. Not part of the public API. */
final class RequestValidation {

    private RequestValidation() {
    }

    static void requireNonBlank(String owner, String field, String value, String label) {
        if (value == null || value.trim().isEmpty()) {
            throw new Daraja4jValidationException(
                    owner + " is missing: " + label + ". Set it via " + owner + ".builder()." + field + "(...).");
        }
    }

    static void requireResolved(String owner, String field, String value, String hint) {
        if (value == null || value.trim().isEmpty()) {
            throw new Daraja4jValidationException(
                    owner + " is missing a '" + field + "' value and no default is configured. Set it via " + hint + ".");
        }
    }

    static String firstNonBlank(String a, String b) {
        return (a != null && !a.trim().isEmpty()) ? a : b;
    }
}
