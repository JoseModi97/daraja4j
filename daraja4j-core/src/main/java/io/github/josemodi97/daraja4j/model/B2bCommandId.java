package io.github.josemodi97.daraja4j.model;

/** Daraja's B2B {@code CommandID} values. */
public enum B2bCommandId {
    BUSINESS_PAY_BILL("BusinessPayBill"),
    BUSINESS_BUY_GOODS("BusinessBuyGoods");

    private final String wireValue;

    B2bCommandId(String wireValue) {
        this.wireValue = wireValue;
    }

    public String getWireValue() {
        return wireValue;
    }
}
