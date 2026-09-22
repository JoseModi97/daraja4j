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
 * A payment from a business shortcode to a Pochi la Biashara-enabled
 * personal till. Wire-compatible with {@link B2cRequest} (same endpoint,
 * same fields) - kept as its own type for discoverability, since Daraja
 * documents it as a distinct use case from a general B2C disbursement.
 */
public final class B2PochiRequest {

    private final String originatorConversationId;
    private final String initiatorName;
    private final String securityCredential;
    private final BigDecimal amount;
    private final String partyA;
    private final String partyB;
    private final String remarks;
    private final String queueTimeoutUrl;
    private final String resultUrl;
    private final String occasion;

    private B2PochiRequest(Builder builder) {
        this.originatorConversationId = builder.originatorConversationId != null
                ? builder.originatorConversationId : UUID.randomUUID().toString();
        this.initiatorName = builder.initiatorName;
        this.securityCredential = builder.securityCredential;
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
        requireNonBlank("B2PochiRequest", "amount", amount == null ? null : amount.toPlainString(), "amount (how much to pay)");
        requireNonBlank("B2PochiRequest", "partyB", partyB, "partyB (the recipient Pochi la Biashara till/phone number)");
        requireNonBlank("B2PochiRequest", "remarks", remarks, "remarks (2-100 characters describing the payment)");
    }

    public String toJson(Daraja4jConfig config) {
        validate();

        String resolvedPartyA = firstNonBlank(partyA, config.getDefaultShortcode());
        String resolvedInitiatorName = firstNonBlank(initiatorName, config.getInitiatorName());
        String resolvedSecurityCredential = firstNonBlank(securityCredential, config.getSecurityCredential());
        String resolvedQueueTimeoutUrl = firstNonBlank(queueTimeoutUrl, config.getDefaultQueueTimeoutUrl());
        String resolvedResultUrl = firstNonBlank(resultUrl, config.getDefaultResultUrl());
        requireResolved("B2PochiRequest", "partyA", resolvedPartyA,
                "B2PochiRequest.builder().partyA(...) or Daraja4jConfig.builder().defaultShortcode(...)");
        requireResolved("B2PochiRequest", "initiatorName", resolvedInitiatorName,
                "B2PochiRequest.builder().initiatorName(...) or Daraja4jConfig.builder().initiatorName(...)");
        requireResolved("B2PochiRequest", "securityCredential", resolvedSecurityCredential,
                "B2PochiRequest.builder().securityCredential(...) or Daraja4jConfig.builder().securityCredential(...)");
        requireResolved("B2PochiRequest", "queueTimeoutUrl", resolvedQueueTimeoutUrl,
                "B2PochiRequest.builder().queueTimeoutUrl(...) or Daraja4jConfig.builder().defaultQueueTimeoutUrl(...)");
        requireResolved("B2PochiRequest", "resultUrl", resolvedResultUrl,
                "B2PochiRequest.builder().resultUrl(...) or Daraja4jConfig.builder().defaultResultUrl(...)");

        return new JsonWriter()
                .field("OriginatorConversationID", originatorConversationId)
                .field("InitiatorName", resolvedInitiatorName)
                .field("SecurityCredential", resolvedSecurityCredential)
                .field("CommandID", B2cCommandId.BUSINESS_PAYMENT.getWireValue())
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

    /** Builder for {@link B2PochiRequest}. */
    public static final class Builder {
        private String originatorConversationId;
        private String initiatorName;
        private String securityCredential;
        private BigDecimal amount;
        private String partyA;
        private String partyB;
        private String remarks;
        private String queueTimeoutUrl;
        private String resultUrl;
        private String occasion;

        private Builder() {
        }

        public Builder originatorConversationId(String originatorConversationId) {
            this.originatorConversationId = originatorConversationId;
            return this;
        }

        public Builder initiatorName(String initiatorName) {
            this.initiatorName = initiatorName;
            return this;
        }

        public Builder securityCredential(String securityCredential) {
            this.securityCredential = securityCredential;
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

        public Builder partyA(String partyA) {
            this.partyA = partyA;
            return this;
        }

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

        public B2PochiRequest build() {
            return new B2PochiRequest(this);
        }
    }
}
