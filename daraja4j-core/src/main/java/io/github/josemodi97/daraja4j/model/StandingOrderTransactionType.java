package io.github.josemodi97.daraja4j.model;

/** Which receiver type an M-Pesa Ratiba standing order pays. */
public enum StandingOrderTransactionType {
    PAY_BILL("Standing Order Pay Bill Ext-Third Party", IdentifierType.SHORTCODE),
    BUY_GOODS("Standing Order Merchant Payment Ext-Third Party", IdentifierType.TILL_NUMBER);

    private final String wireValue;
    private final IdentifierType receiverPartyIdentifierType;

    StandingOrderTransactionType(String wireValue, IdentifierType receiverPartyIdentifierType) {
        this.wireValue = wireValue;
        this.receiverPartyIdentifierType = receiverPartyIdentifierType;
    }

    public String getWireValue() {
        return wireValue;
    }

    /** The matching {@code ReceiverPartyIdentifierType}: shortcode for pay bill, till number for buy goods. */
    public IdentifierType getReceiverPartyIdentifierType() {
        return receiverPartyIdentifierType;
    }
}
