package io.github.josemodi97.daraja4j.model;

/** Daraja's {@code IdentifierType} / {@code SenderIdentifierType} / {@code RecieverIdentifierType} codes. */
public enum IdentifierType {
    MSISDN(1),
    TILL_NUMBER(2),
    SHORTCODE(4);

    private final int code;

    IdentifierType(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    /** The value Daraja expects on the wire, e.g. {@code "4"}. */
    public String getWireValue() {
        return String.valueOf(code);
    }
}
