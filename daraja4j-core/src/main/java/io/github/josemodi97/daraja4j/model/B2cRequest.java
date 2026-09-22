package io.github.josemodi97.daraja4j.model;

import static io.github.josemodi97.daraja4j.model.RequestValidation.firstNonBlank;
import static io.github.josemodi97.daraja4j.model.RequestValidation.requireNonBlank;
import static io.github.josemodi97.daraja4j.model.RequestValidation.requireResolved;

import io.github.josemodi97.daraja4j.Daraja4jConfig;
import io.github.josemodi97.daraja4j.internal.JsonWriter;
import io.github.josemodi97.daraja4j.util.PhoneNormalizer;
import java.math.BigDecimal;
import java.util.UUID;

/**
 * A Business-to-Customer disbursement: sends money from a B2C-enabled
 * shortcode to a registered M-Pesa customer (salary, promotion, or general
 * business payment). Requires {@link Daraja4jConfig#getInitiatorName()} and
 * {@link Daraja4jConfig#getSecurityCredential()} (or the per-request
 * overrides below) to be set.
 */
public final class B2cRequest {

    private final String originatorConversationId;
    private final String initiatorName;
    private final String securityCredential;
    private final B2cCommandId commandId;
    private final BigDecimal amount;
    private final String partyA;
    private final String partyB;
    private final String remarks;
    private final String queueTimeoutUrl;
    private final String resultUrl;
    private final String occasion;

    private B2cRequest(Builder builder) {
        this.originatorConversationId = builder.originatorConversationId != null
                ? builder.originatorConversationId : UUID.randomUUID().toString();
        this.initiatorName = builder.initiatorName;
        this.securityCredential = builder.securityCredential;
        this.commandId = builder.commandId != null ? builder.commandId : B2cCommandId.BUSINESS_PAYMENT;
        this.amount = builder.amount;
        this.partyA = builder.partyA;
        this.partyB = builder.partyB;
        this.remarks = builder.remarks;
        this.queueTimeoutUrl = builder.queueTimeoutUrl;
        this.resultUrl = builder.resultUrl;
        this.occasion = builder.occasion;
    }

    public static Builder builder() {
        return new Builder();
    }

    public void validate() {
        requireNonBlank("B2cRequest", "amount", amount == null ? null : amount.toPlainString(), "amount (how much to disburse)");
        requireNonBlank("B2cRequest", "partyB", partyB, "partyB (the recipient's phone number)");
        requireNonBlank("B2cRequest", "remarks", remarks, "remarks (2-100 characters describing the payment)");
    }

    public String toJson(Daraja4jConfig config) {
        validate();

        String resolvedPartyA = firstNonBlank(partyA, config.getDefaultShortcode());
        String resolvedInitiatorName = firstNonBlank(initiatorName, config.getInitiatorName());
        String resolvedSecurityCredential = firstNonBlank(securityCredential, config.getSecurityCredential());
        String resolvedQueueTimeoutUrl = firstNonBlank(queueTimeoutUrl, config.getDefaultQueueTimeoutUrl());
        String resolvedResultUrl = firstNonBlank(resultUrl, config.getDefaultResultUrl());
        requireResolved("B2cRequest", "partyA", resolvedPartyA,
                "B2cRequest.builder().partyA(...) or Daraja4jConfig.builder().defaultShortcode(...)");
        requireResolved("B2cRequest", "initiatorName", resolvedInitiatorName,
                "B2cRequest.builder().initiatorName(...) or Daraja4jConfig.builder().initiatorName(...)");
        requireResolved("B2cRequest", "securityCredential", resolvedSecurityCredential,
                "B2cRequest.builder().securityCredential(...) or Daraja4jConfig.builder().securityCredential(...)");
        requireResolved("B2cRequest", "queueTimeoutUrl", resolvedQueueTimeoutUrl,
                "B2cRequest.builder().queueTimeoutUrl(...) or Daraja4jConfig.builder().defaultQueueTimeoutUrl(...)");
        requireResolved("B2cRequest", "resultUrl", resolvedResultUrl,
                "B2cRequest.builder().resultUrl(...) or Daraja4jConfig.builder().defaultResultUrl(...)");

        return new JsonWriter()
                .field("OriginatorConversationID", originatorConversationId)
                .field("InitiatorName", resolvedInitiatorName)
                .field("SecurityCredential", resolvedSecurityCredential)
                .field("CommandID", commandId.getWireValue())
                .field("Amount", amount.toPlainString())
                .field("PartyA", resolvedPartyA)
                .field("PartyB", PhoneNormalizer.normalize(partyB))
                .field("Remarks", remarks)
                .field("QueueTimeOutURL", resolvedQueueTimeoutUrl)
                .field("ResultURL", resolvedResultUrl)
                .field("Occassion", occasion)
                .build();
    }

    public String getOriginatorConversationId() {
        return originatorConversationId;
    }

    public String getInitiatorName() {
        return initiatorName;
    }

    public String getSecurityCredential() {
        return securityCredential;
    }

    public B2cCommandId getCommandId() {
        return commandId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public String getPartyA() {
        return partyA;
    }

    public String getPartyB() {
        return partyB;
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

    /** Builder for {@link B2cRequest}. */
    public static final class Builder {
        private String originatorConversationId;
        private String initiatorName;
        private String securityCredential;
        private B2cCommandId commandId;
        private BigDecimal amount;
        private String partyA;
        private String partyB;
        private String remarks;
        private String queueTimeoutUrl;
        private String resultUrl;
        private String occasion;

        private Builder() {
        }

        /** A unique ID for this disbursement, used to avoid double-payment; a random UUID is generated if not set. */
        public Builder originatorConversationId(String originatorConversationId) {
            this.originatorConversationId = originatorConversationId;
            return this;
        }

        /** Overrides {@link Daraja4jConfig#getInitiatorName()} for this request. */
        public Builder initiatorName(String initiatorName) {
            this.initiatorName = initiatorName;
            return this;
        }

        /** Overrides {@link Daraja4jConfig#getSecurityCredential()} for this request. */
        public Builder securityCredential(String securityCredential) {
            this.securityCredential = securityCredential;
            return this;
        }

        /** {@code BUSINESS_PAYMENT} (default), {@code SALARY_PAYMENT}, or {@code PROMOTION_PAYMENT}. */
        public Builder commandId(B2cCommandId commandId) {
            this.commandId = commandId;
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

        /** The B2C shortcode money is sent from. Overrides {@link Daraja4jConfig#getDefaultShortcode()} for this request. */
        public Builder partyA(String partyA) {
            this.partyA = partyA;
            return this;
        }

        /** The recipient's phone number. */
        public Builder partyB(String partyB) {
            this.partyB = partyB;
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

        public B2cRequest build() {
            return new B2cRequest(this);
        }
    }
}
