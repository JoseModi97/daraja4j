package io.github.josemodi97.daraja4j.model;

/** Daraja's C2B {@code CommandID} / {@code TransactionType} values. */
public enum C2bTransactionType {
    CUSTOMER_PAY_BILL_ONLINE("CustomerPayBillOnline"),
    CUSTOMER_BUY_GOODS_ONLINE("CustomerBuyGoodsOnline");

    private final String wireValue;

    C2bTransactionType(String wireValue) {
        this.wireValue = wireValue;
    }

    public String getWireValue() {
        return wireValue;
    }
}
