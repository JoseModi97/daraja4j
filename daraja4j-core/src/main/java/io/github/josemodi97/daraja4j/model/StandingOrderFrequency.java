package io.github.josemodi97.daraja4j.model;

/** How often an M-Pesa Ratiba standing order executes. */
public enum StandingOrderFrequency {
    ONE_OFF(1),
    DAILY(2),
    WEEKLY(3),
    BI_WEEKLY(4),
    MONTHLY(5),
    BI_MONTHLY(6),
    QUARTERLY(7),
    HALF_YEARLY(8),
    YEARLY(9);

    private final int code;

    StandingOrderFrequency(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public String getWireValue() {
        return String.valueOf(code);
    }
}
