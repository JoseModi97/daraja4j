package io.github.josemodi97.daraja4j.model;

import static io.github.josemodi97.daraja4j.model.RequestValidation.firstNonBlank;
import static io.github.josemodi97.daraja4j.model.RequestValidation.requireNonBlank;
import static io.github.josemodi97.daraja4j.model.RequestValidation.requireResolved;

import io.github.josemodi97.daraja4j.Daraja4jConfig;
import io.github.josemodi97.daraja4j.internal.JsonWriter;
import io.github.josemodi97.daraja4j.util.PhoneNormalizer;
import java.math.BigDecimal;

/**
 * Simulates an inbound C2B payment against {@code shortcode} - <strong>sandbox
 * only</strong>. Daraja delivers the simulated payment to the
 * {@code confirmationUrl} registered with {@link RegisterC2bUrlsRequest},
 * letting you test your confirmation handler without a real payment.
 */
public final class C2bSimulateRequest {

    private final String shortcode;
    private final BigDecimal amount;
    private final String msisdn;
    private final String billRefNumber;
    private final C2bTransactionType commandId;

    private C2bSimulateRequest(Builder builder) {
        this.shortcode = builder.shortcode;
        this.amount = builder.amount;
        this.msisdn = builder.msisdn;
        this.billRefNumber = builder.billRefNumber;
        this.commandId = builder.commandId != null ? builder.commandId : C2bTransactionType.CUSTOMER_PAY_BILL_ONLINE;
    }

    public static Builder builder() {
        return new Builder();
    }

    public void validate() {
        requireNonBlank("C2bSimulateRequest", "amount", amount == null ? null : amount.toPlainString(),
                "amount (how much the simulated payer sends)");
        requireNonBlank("C2bSimulateRequest", "msisdn", msisdn, "msisdn (the simulated payer's phone number)");
    }

    public String toJson(Daraja4jConfig config) {
        validate();

        String resolvedShortcode = firstNonBlank(shortcode, config.getDefaultShortcode());
        requireResolved("C2bSimulateRequest", "shortcode", resolvedShortcode,
                "C2bSimulateRequest.builder().shortcode(...) or Daraja4jConfig.builder().defaultShortcode(...)");

        JsonWriter writer = new JsonWriter()
                .field("ShortCode", resolvedShortcode)
                .field("CommandID", commandId.getWireValue())
                .field("Amount", amount);
        writer.field("Msisdn", PhoneNormalizer.normalize(msisdn));
        if (commandId == C2bTransactionType.CUSTOMER_PAY_BILL_ONLINE) {
            writer.field("BillRefNumber", billRefNumber);
        }
        return writer.build();
    }

    public String getShortcode() {
        return shortcode;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public String getMsisdn() {
        return msisdn;
    }

    public String getBillRefNumber() {
        return billRefNumber;
    }

    public C2bTransactionType getCommandId() {
        return commandId;
    }

    /** Builder for {@link C2bSimulateRequest}. */
    public static final class Builder {
        private String shortcode;
        private BigDecimal amount;
        private String msisdn;
        private String billRefNumber;
        private C2bTransactionType commandId;

        private Builder() {
        }

        public Builder shortcode(String shortcode) {
            this.shortcode = shortcode;
            return this;
        }

        public Builder amount(BigDecimal amount) {
            this.amount = amount;
            return this;
        }

        public Builder amount(double amount) {
            this.amount = BigDecimal.valueOf(amount);
            return this;
        }

        public Builder msisdn(String msisdn) {
            this.msisdn = msisdn;
            return this;
        }

        /** Account reference for a simulated pay-bill payment; ignored for buy-goods. */
        public Builder billRefNumber(String billRefNumber) {
            this.billRefNumber = billRefNumber;
            return this;
        }

        /** {@code CUSTOMER_PAY_BILL_ONLINE} (default) or {@code CUSTOMER_BUY_GOODS_ONLINE}. */
        public Builder commandId(C2bTransactionType commandId) {
            this.commandId = commandId;
            return this;
        }

        public C2bSimulateRequest build() {
            return new C2bSimulateRequest(this);
        }
    }
}
