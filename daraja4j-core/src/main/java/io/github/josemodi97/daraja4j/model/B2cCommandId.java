package io.github.josemodi97.daraja4j.model;

/** Daraja's B2C/B2B/B2Pochi {@code CommandID} values. */
public enum B2cCommandId {
    SALARY_PAYMENT("SalaryPayment"),
    BUSINESS_PAYMENT("BusinessPayment"),
    PROMOTION_PAYMENT("PromotionPayment");

    private final String wireValue;

    B2cCommandId(String wireValue) {
        this.wireValue = wireValue;
    }

    public String getWireValue() {
        return wireValue;
    }
}
