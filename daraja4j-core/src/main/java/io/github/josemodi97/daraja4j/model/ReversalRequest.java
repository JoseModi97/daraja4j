package io.github.josemodi97.daraja4j.model;

import static io.github.josemodi97.daraja4j.model.RequestValidation.firstNonBlank;
import static io.github.josemodi97.daraja4j.model.RequestValidation.requireNonBlank;
import static io.github.josemodi97.daraja4j.model.RequestValidation.requireResolved;

import io.github.josemodi97.daraja4j.Daraja4jConfig;
import io.github.josemodi97.daraja4j.internal.JsonWriter;
import java.math.BigDecimal;

/** Reverses a completed C2B transaction, refunding the payer. */
public final class ReversalRequest {

    private static final String RECEIVER_IDENTIFIER_TYPE = "11";

    private final String initiator;
    private final String securityCredential;
    private final String transactionId;
    private final BigDecimal amount;
    private final String receiverParty;
    private final String remarks;
    private final String queueTimeoutUrl;
    private final String resultUrl;
    private final String occasion;

    private ReversalRequest(Builder builder) {
        this.initiator = builder.initiator;
        this.securityCredential = builder.securityCredential;
        this.transactionId = builder.transactionId;
        this.amount = builder.amount;
        this.receiverParty = builder.receiverParty;
        this.remarks = builder.remarks;
        this.queueTimeoutUrl = builder.queueTimeoutUrl;
        this.resultUrl = builder.resultUrl;
        this.occasion = builder.occasion;
    }

    public static Builder builder() {
        return new Builder();
    }

    public void validate() {
        requireNonBlank("ReversalRequest", "transactionId", transactionId,
                "transactionId (the M-Pesa receipt number of the transaction to reverse)");
        requireNonBlank("ReversalRequest", "amount", amount == null ? null : amount.toPlainString(),
                "amount (must match the original transaction amount)");
        requireNonBlank("ReversalRequest", "remarks", remarks, "remarks (2-100 characters)");
    }

    public String toJson(Daraja4jConfig config) {
        validate();

        String resolvedReceiverParty = firstNonBlank(receiverParty, config.getDefaultShortcode());
        String resolvedInitiator = firstNonBlank(initiator, config.getInitiatorName());
        String resolvedSecurityCredential = firstNonBlank(securityCredential, config.getSecurityCredential());
        String resolvedQueueTimeoutUrl = firstNonBlank(queueTimeoutUrl, config.getDefaultQueueTimeoutUrl());
        String resolvedResultUrl = firstNonBlank(resultUrl, config.getDefaultResultUrl());
        requireResolved("ReversalRequest", "receiverParty", resolvedReceiverParty,
                "ReversalRequest.builder().receiverParty(...) or Daraja4jConfig.builder().defaultShortcode(...)");
        requireResolved("ReversalRequest", "initiator", resolvedInitiator,
                "ReversalRequest.builder().initiator(...) or Daraja4jConfig.builder().initiatorName(...)");
        requireResolved("ReversalRequest", "securityCredential", resolvedSecurityCredential,
                "ReversalRequest.builder().securityCredential(...) or Daraja4jConfig.builder().securityCredential(...)");
        requireResolved("ReversalRequest", "queueTimeoutUrl", resolvedQueueTimeoutUrl,
                "ReversalRequest.builder().queueTimeoutUrl(...) or Daraja4jConfig.builder().defaultQueueTimeoutUrl(...)");
        requireResolved("ReversalRequest", "resultUrl", resolvedResultUrl,
                "ReversalRequest.builder().resultUrl(...) or Daraja4jConfig.builder().defaultResultUrl(...)");

        return new JsonWriter()
                .field("Initiator", resolvedInitiator)
                .field("SecurityCredential", resolvedSecurityCredential)
                .field("CommandID", "TransactionReversal")
                .field("TransactionID", transactionId)
                .field("Amount", amount.toPlainString())
                .field("ReceiverParty", resolvedReceiverParty)
                .field("RecieverIdentifierType", RECEIVER_IDENTIFIER_TYPE)
                .field("ResultURL", resolvedResultUrl)
                .field("QueueTimeOutURL", resolvedQueueTimeoutUrl)
                .field("Remarks", remarks)
                .field("Occasion", occasion)
                .build();
    }

    public String getInitiator() {
        return initiator;
    }

    public String getSecurityCredential() {
        return securityCredential;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public String getReceiverParty() {
        return receiverParty;
    }

    public String getRemarks() {
        return remarks;
    }

    public String getQueueTimeoutUrl() {
        return queueTimeoutUrl;
    }

    public String getResultUrl() {
        return resultUrl;
    }

    public String getOccasion() {
        return occasion;
    }

    /** Builder for {@link ReversalRequest}. */
    public static final class Builder {
        private String initiator;
        private String securityCredential;
        private String transactionId;
        private BigDecimal amount;
        private String receiverParty;
        private String remarks;
        private String queueTimeoutUrl;
        private String resultUrl;
        private String occasion;

        private Builder() {
        }

        public Builder initiator(String initiator) {
            this.initiator = initiator;
            return this;
        }

        public Builder securityCredential(String securityCredential) {
            this.securityCredential = securityCredential;
            return this;
        }

        public Builder transactionId(String transactionId) {
            this.transactionId = transactionId;
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

        /** The shortcode the transaction was paid to. Overrides {@link Daraja4jConfig#getDefaultShortcode()} for this request. */
        public Builder receiverParty(String receiverParty) {
            this.receiverParty = receiverParty;
            return this;
        }

        public Builder remarks(String remarks) {
            this.remarks = remarks;
            return this;
        }

        public Builder queueTimeoutUrl(String queueTimeoutUrl) {
            this.queueTimeoutUrl = queueTimeoutUrl;
            return this;
        }

        public Builder resultUrl(String resultUrl) {
            this.resultUrl = resultUrl;
            return this;
        }

        public Builder occasion(String occasion) {
            this.occasion = occasion;
            return this;
        }

        public ReversalRequest build() {
            return new ReversalRequest(this);
        }
    }
}
