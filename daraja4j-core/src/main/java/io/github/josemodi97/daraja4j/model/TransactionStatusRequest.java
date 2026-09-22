package io.github.josemodi97.daraja4j.model;

import static io.github.josemodi97.daraja4j.model.RequestValidation.firstNonBlank;
import static io.github.josemodi97.daraja4j.model.RequestValidation.requireNonBlank;
import static io.github.josemodi97.daraja4j.model.RequestValidation.requireResolved;

import io.github.josemodi97.daraja4j.Daraja4jConfig;
import io.github.josemodi97.daraja4j.internal.JsonWriter;

/**
 * Checks the status of a prior transaction on this account - a secondary
 * reconciliation mechanism for when a callback was missed. Identify the
 * transaction with either {@code transactionId} (the M-Pesa receipt number)
 * or {@code originalConversationId}.
 */
public final class TransactionStatusRequest {

    private final String initiator;
    private final String securityCredential;
    private final String transactionId;
    private final String originalConversationId;
    private final String partyA;
    private final IdentifierType identifierType;
    private final String remarks;
    private final String occasion;
    private final String queueTimeoutUrl;
    private final String resultUrl;

    private TransactionStatusRequest(Builder builder) {
        this.initiator = builder.initiator;
        this.securityCredential = builder.securityCredential;
        this.transactionId = builder.transactionId;
        this.originalConversationId = builder.originalConversationId;
        this.partyA = builder.partyA;
        this.identifierType = builder.identifierType != null ? builder.identifierType : IdentifierType.SHORTCODE;
        this.remarks = builder.remarks;
        this.occasion = builder.occasion;
        this.queueTimeoutUrl = builder.queueTimeoutUrl;
        this.resultUrl = builder.resultUrl;
    }

    public static Builder builder() {
        return new Builder();
    }

    public void validate() {
        requireNonBlank("TransactionStatusRequest", "remarks", remarks, "remarks (up to 100 characters)");
        if ((transactionId == null || transactionId.trim().isEmpty())
                && (originalConversationId == null || originalConversationId.trim().isEmpty())) {
            throw new io.github.josemodi97.daraja4j.exception.Daraja4jValidationException(
                    "TransactionStatusRequest needs either transactionId(...) (the M-Pesa receipt number) "
                            + "or originalConversationId(...) to identify which transaction to check.");
        }
    }

    public String toJson(Daraja4jConfig config) {
        validate();

        String resolvedPartyA = firstNonBlank(partyA, config.getDefaultShortcode());
        String resolvedInitiator = firstNonBlank(initiator, config.getInitiatorName());
        String resolvedSecurityCredential = firstNonBlank(securityCredential, config.getSecurityCredential());
        String resolvedQueueTimeoutUrl = firstNonBlank(queueTimeoutUrl, config.getDefaultQueueTimeoutUrl());
        String resolvedResultUrl = firstNonBlank(resultUrl, config.getDefaultResultUrl());
        requireResolved("TransactionStatusRequest", "partyA", resolvedPartyA,
                "TransactionStatusRequest.builder().partyA(...) or Daraja4jConfig.builder().defaultShortcode(...)");
        requireResolved("TransactionStatusRequest", "initiator", resolvedInitiator,
                "TransactionStatusRequest.builder().initiator(...) or Daraja4jConfig.builder().initiatorName(...)");
        requireResolved("TransactionStatusRequest", "securityCredential", resolvedSecurityCredential,
                "TransactionStatusRequest.builder().securityCredential(...) or Daraja4jConfig.builder().securityCredential(...)");
        requireResolved("TransactionStatusRequest", "queueTimeoutUrl", resolvedQueueTimeoutUrl,
                "TransactionStatusRequest.builder().queueTimeoutUrl(...) or Daraja4jConfig.builder().defaultQueueTimeoutUrl(...)");
        requireResolved("TransactionStatusRequest", "resultUrl", resolvedResultUrl,
                "TransactionStatusRequest.builder().resultUrl(...) or Daraja4jConfig.builder().defaultResultUrl(...)");

        return new JsonWriter()
                .field("Initiator", resolvedInitiator)
                .field("SecurityCredential", resolvedSecurityCredential)
                .field("CommandID", "TransactionStatusQuery")
                .field("TransactionID", transactionId)
                .field("OriginalConversationID", originalConversationId)
                .field("PartyA", resolvedPartyA)
                .field("IdentifierType", identifierType.getWireValue())
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

    public String getOriginalConversationId() {
        return originalConversationId;
    }

    public String getPartyA() {
        return partyA;
    }

    public IdentifierType getIdentifierType() {
        return identifierType;
    }

    public String getRemarks() {
        return remarks;
    }

    public String getOccasion() {
        return occasion;
    }

    public String getQueueTimeoutUrl() {
        return queueTimeoutUrl;
    }

    public String getResultUrl() {
        return resultUrl;
    }

    /** Builder for {@link TransactionStatusRequest}. */
    public static final class Builder {
        private String initiator;
        private String securityCredential;
        private String transactionId;
        private String originalConversationId;
        private String partyA;
        private IdentifierType identifierType;
        private String remarks;
        private String occasion;
        private String queueTimeoutUrl;
        private String resultUrl;

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

        public Builder originalConversationId(String originalConversationId) {
            this.originalConversationId = originalConversationId;
            return this;
        }

        /** The org/MSISDN the transaction was made against. Overrides {@link Daraja4jConfig#getDefaultShortcode()} for this request. */
        public Builder partyA(String partyA) {
            this.partyA = partyA;
            return this;
        }

        public Builder identifierType(IdentifierType identifierType) {
            this.identifierType = identifierType;
            return this;
        }

        public Builder remarks(String remarks) {
            this.remarks = remarks;
            return this;
        }

        public Builder occasion(String occasion) {
            this.occasion = occasion;
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

        public TransactionStatusRequest build() {
            return new TransactionStatusRequest(this);
        }
    }
}
