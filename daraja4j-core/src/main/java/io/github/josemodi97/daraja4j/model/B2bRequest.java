package io.github.josemodi97.daraja4j.model;

import static io.github.josemodi97.daraja4j.model.RequestValidation.firstNonBlank;
import static io.github.josemodi97.daraja4j.model.RequestValidation.requireNonBlank;
import static io.github.josemodi97.daraja4j.model.RequestValidation.requireResolved;

import io.github.josemodi97.daraja4j.Daraja4jConfig;
import io.github.josemodi97.daraja4j.internal.JsonWriter;

/**
 * A Business-to-Business payment: pays another organization's pay bill or
 * till (Business Buy Goods) directly from this shortcode's working account,
 * optionally on behalf of a named consumer ({@link Builder#requester(String)}).
 */
public final class B2bRequest {

    private final String initiator;
    private final String securityCredential;
    private final B2bCommandId commandId;
    private final IdentifierType senderIdentifierType;
    private final IdentifierType receiverIdentifierType;
    private final java.math.BigDecimal amount;
    private final String partyA;
    private final String partyB;
    private final String accountReference;
    private final String requester;
    private final String remarks;
    private final String queueTimeoutUrl;
    private final String resultUrl;
    private final String occasion;

    private B2bRequest(Builder builder) {
        this.initiator = builder.initiator;
        this.securityCredential = builder.securityCredential;
        this.commandId = builder.commandId != null ? builder.commandId : B2bCommandId.BUSINESS_PAY_BILL;
        this.senderIdentifierType = IdentifierType.SHORTCODE;
        this.receiverIdentifierType = IdentifierType.SHORTCODE;
        this.amount = builder.amount;
        this.partyA = builder.partyA;
        this.partyB = builder.partyB;
        this.accountReference = builder.accountReference;
        this.requester = builder.requester;
        this.remarks = builder.remarks;
        this.queueTimeoutUrl = builder.queueTimeoutUrl;
        this.resultUrl = builder.resultUrl;
        this.occasion = builder.occasion;
    }

    public static Builder builder() {
        return new Builder();
    }

    public void validate() {
        requireNonBlank("B2bRequest", "amount", amount == null ? null : amount.toPlainString(), "amount (how much to pay)");
        requireNonBlank("B2bRequest", "partyB", partyB, "partyB (the receiving organization's shortcode)");
        requireNonBlank("B2bRequest", "accountReference", accountReference, "accountReference (up to 13 characters)");
        requireNonBlank("B2bRequest", "remarks", remarks, "remarks (2-100 characters describing the payment)");
    }

    public String toJson(Daraja4jConfig config) {
        validate();

        String resolvedPartyA = firstNonBlank(partyA, config.getDefaultShortcode());
        String resolvedInitiator = firstNonBlank(initiator, config.getInitiatorName());
        String resolvedSecurityCredential = firstNonBlank(securityCredential, config.getSecurityCredential());
        String resolvedQueueTimeoutUrl = firstNonBlank(queueTimeoutUrl, config.getDefaultQueueTimeoutUrl());
        String resolvedResultUrl = firstNonBlank(resultUrl, config.getDefaultResultUrl());
        requireResolved("B2bRequest", "partyA", resolvedPartyA,
                "B2bRequest.builder().partyA(...) or Daraja4jConfig.builder().defaultShortcode(...)");
        requireResolved("B2bRequest", "initiator", resolvedInitiator,
                "B2bRequest.builder().initiator(...) or Daraja4jConfig.builder().initiatorName(...)");
        requireResolved("B2bRequest", "securityCredential", resolvedSecurityCredential,
                "B2bRequest.builder().securityCredential(...) or Daraja4jConfig.builder().securityCredential(...)");
        requireResolved("B2bRequest", "queueTimeoutUrl", resolvedQueueTimeoutUrl,
                "B2bRequest.builder().queueTimeoutUrl(...) or Daraja4jConfig.builder().defaultQueueTimeoutUrl(...)");
        requireResolved("B2bRequest", "resultUrl", resolvedResultUrl,
                "B2bRequest.builder().resultUrl(...) or Daraja4jConfig.builder().defaultResultUrl(...)");

        return new JsonWriter()
                .field("Initiator", resolvedInitiator)
                .field("SecurityCredential", resolvedSecurityCredential)
                .field("CommandID", commandId.getWireValue())
                .field("SenderIdentifierType", senderIdentifierType.getWireValue())
                .field("RecieverIdentifierType", receiverIdentifierType.getWireValue())
                .field("Amount", amount.toPlainString())
                .field("PartyA", resolvedPartyA)
                .field("PartyB", partyB)
                .field("AccountReference", accountReference)
                .field("Requester", requester)
                .field("Remarks", remarks)
                .field("QueueTimeOutURL", resolvedQueueTimeoutUrl)
                .field("ResultURL", resolvedResultUrl)
                .field("Occassion", occasion)
                .build();
    }

    public String getInitiator() {
        return initiator;
    }

    public String getSecurityCredential() {
        return securityCredential;
    }

    public B2bCommandId getCommandId() {
        return commandId;
    }

    public java.math.BigDecimal getAmount() {
        return amount;
    }

    public String getPartyA() {
        return partyA;
    }

    public String getPartyB() {
        return partyB;
    }

    public String getAccountReference() {
        return accountReference;
    }

    public String getRequester() {
        return requester;
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

    /** Builder for {@link B2bRequest}. */
    public static final class Builder {
        private String initiator;
        private String securityCredential;
        private B2bCommandId commandId;
        private java.math.BigDecimal amount;
        private String partyA;
        private String partyB;
        private String accountReference;
        private String requester;
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

        /** {@code BUSINESS_PAY_BILL} (default) or {@code BUSINESS_BUY_GOODS}. */
        public Builder commandId(B2bCommandId commandId) {
            this.commandId = commandId;
            return this;
        }

        public Builder amount(java.math.BigDecimal amount) {
            this.amount = amount;
            return this;
        }

        public Builder amount(double amount) {
            this.amount = java.math.BigDecimal.valueOf(amount);
            return this;
        }

        /** The paying shortcode. Overrides {@link Daraja4jConfig#getDefaultShortcode()} for this request. */
        public Builder partyA(String partyA) {
            this.partyA = partyA;
            return this;
        }

        /** The receiving organization's shortcode. */
        public Builder partyB(String partyB) {
            this.partyB = partyB;
            return this;
        }

        public Builder accountReference(String accountReference) {
            this.accountReference = accountReference;
            return this;
        }

        /** Optional: the consumer's phone number, when paying on their behalf. */
        public Builder requester(String requester) {
            this.requester = requester;
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

        public B2bRequest build() {
            return new B2bRequest(this);
        }
    }
}
