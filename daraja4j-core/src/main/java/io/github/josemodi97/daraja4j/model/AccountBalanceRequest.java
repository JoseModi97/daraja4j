package io.github.josemodi97.daraja4j.model;

import static io.github.josemodi97.daraja4j.model.RequestValidation.firstNonBlank;
import static io.github.josemodi97.daraja4j.model.RequestValidation.requireNonBlank;
import static io.github.josemodi97.daraja4j.model.RequestValidation.requireResolved;

import io.github.josemodi97.daraja4j.Daraja4jConfig;
import io.github.josemodi97.daraja4j.internal.JsonWriter;

/** Queries the current M-Pesa account balance of this shortcode. The result arrives at {@code resultUrl}. */
public final class AccountBalanceRequest {

    private final String initiator;
    private final String securityCredential;
    private final String partyA;
    private final IdentifierType identifierType;
    private final String remarks;
    private final String queueTimeoutUrl;
    private final String resultUrl;

    private AccountBalanceRequest(Builder builder) {
        this.initiator = builder.initiator;
        this.securityCredential = builder.securityCredential;
        this.partyA = builder.partyA;
        this.identifierType = builder.identifierType != null ? builder.identifierType : IdentifierType.SHORTCODE;
        this.remarks = builder.remarks;
        this.queueTimeoutUrl = builder.queueTimeoutUrl;
        this.resultUrl = builder.resultUrl;
    }

    public static Builder builder() {
        return new Builder();
    }

    public void validate() {
        requireNonBlank("AccountBalanceRequest", "remarks", remarks, "remarks (up to 100 characters)");
    }

    public String toJson(Daraja4jConfig config) {
        validate();

        String resolvedPartyA = firstNonBlank(partyA, config.getDefaultShortcode());
        String resolvedInitiator = firstNonBlank(initiator, config.getInitiatorName());
        String resolvedSecurityCredential = firstNonBlank(securityCredential, config.getSecurityCredential());
        String resolvedQueueTimeoutUrl = firstNonBlank(queueTimeoutUrl, config.getDefaultQueueTimeoutUrl());
        String resolvedResultUrl = firstNonBlank(resultUrl, config.getDefaultResultUrl());
        requireResolved("AccountBalanceRequest", "partyA", resolvedPartyA,
                "AccountBalanceRequest.builder().partyA(...) or Daraja4jConfig.builder().defaultShortcode(...)");
        requireResolved("AccountBalanceRequest", "initiator", resolvedInitiator,
                "AccountBalanceRequest.builder().initiator(...) or Daraja4jConfig.builder().initiatorName(...)");
        requireResolved("AccountBalanceRequest", "securityCredential", resolvedSecurityCredential,
                "AccountBalanceRequest.builder().securityCredential(...) or Daraja4jConfig.builder().securityCredential(...)");
        requireResolved("AccountBalanceRequest", "queueTimeoutUrl", resolvedQueueTimeoutUrl,
                "AccountBalanceRequest.builder().queueTimeoutUrl(...) or Daraja4jConfig.builder().defaultQueueTimeoutUrl(...)");
        requireResolved("AccountBalanceRequest", "resultUrl", resolvedResultUrl,
                "AccountBalanceRequest.builder().resultUrl(...) or Daraja4jConfig.builder().defaultResultUrl(...)");

        return new JsonWriter()
                .field("Initiator", resolvedInitiator)
                .field("SecurityCredential", resolvedSecurityCredential)
                .field("CommandID", "AccountBalance")
                .field("PartyA", resolvedPartyA)
                .field("IdentifierType", identifierType.getWireValue())
                .field("Remarks", remarks)
                .field("QueueTimeOutURL", resolvedQueueTimeoutUrl)
                .field("ResultURL", resolvedResultUrl)
                .build();
    }

    public String getInitiator() {
        return initiator;
    }

    public String getSecurityCredential() {
        return securityCredential;
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

    public String getQueueTimeoutUrl() {
        return queueTimeoutUrl;
    }

    public String getResultUrl() {
        return resultUrl;
    }

    /** Builder for {@link AccountBalanceRequest}. */
    public static final class Builder {
        private String initiator;
        private String securityCredential;
        private String partyA;
        private IdentifierType identifierType;
        private String remarks;
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

        /** The shortcode to check. Overrides {@link Daraja4jConfig#getDefaultShortcode()} for this request. */
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

        public Builder queueTimeoutUrl(String queueTimeoutUrl) {
            this.queueTimeoutUrl = queueTimeoutUrl;
            return this;
        }

        public Builder resultUrl(String resultUrl) {
            this.resultUrl = resultUrl;
            return this;
        }

        public AccountBalanceRequest build() {
            return new AccountBalanceRequest(this);
        }
    }
}
