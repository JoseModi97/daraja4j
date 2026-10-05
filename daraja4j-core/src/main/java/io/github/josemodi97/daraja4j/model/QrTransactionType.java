package io.github.josemodi97.daraja4j.model;

/**
 * The transaction type encoded into an M-Pesa Dynamic QR code.
 */
public enum QrTransactionType {
    /** Buy Goods (Till Number). Wire value {@code "BG"}. */
    BUY_GOODS("BG"),

    /** Withdraw Cash at Agent. Wire value {@code "WA"}. */
    WITHDRAW_AGENT("WA"),

    /** Paybill. Wire value {@code "PB"}. */
    PAYBILL("PB"),

    /** Send Money (P2P). Wire value {@code "SM"}. */
    SEND_MONEY("SM"),

    /** Send to Business. Wire value {@code "SB"}. */
    SEND_TO_BUSINESS("SB");

    private final String wireValue;

    QrTransactionType(String wireValue) {
        this.wireValue = wireValue;
    }

    public String getWireValue() {
        return wireValue;
    }

    public static QrTransactionType fromWireValue(String value) {
        if (value == null) {
            return null;
        }
        for (QrTransactionType type : values()) {
            if (type.wireValue.equalsIgnoreCase(value) || type.name().equalsIgnoreCase(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown QrTransactionType wire value: " + value);
    }
}
