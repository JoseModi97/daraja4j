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
 * Creates an M-Pesa Ratiba standing order: a recurring collection from a
 * customer's M-Pesa wallet on a schedule you define, after one M-Pesa PIN
 * entry to opt in. This is a commercial API - see Safaricom's M-Pesa Ratiba
 * documentation for onboarding.
 */
public final class StandingOrderRequest {

    private final String standingOrderName;
    private final StandingOrderTransactionType transactionType;
    private final String businessShortCode;
    private final String partyA;
    private final BigDecimal amount;
    private final String startDate;
    private final String endDate;
    private final StandingOrderFrequency frequency;
    private final String customStoId;
    private final String accountReference;
    private final String transactionDesc;
    private final String callbackUrl;

    private StandingOrderRequest(Builder builder) {
        this.standingOrderName = builder.standingOrderName;
        this.transactionType = builder.transactionType != null ? builder.transactionType : StandingOrderTransactionType.PAY_BILL;
        this.businessShortCode = builder.businessShortCode;
        this.partyA = builder.partyA;
        this.amount = builder.amount;
        this.startDate = builder.startDate;
        this.endDate = builder.endDate;
        this.frequency = builder.frequency;
        this.customStoId = builder.customStoId != null ? builder.customStoId : UUID.randomUUID().toString();
        this.accountReference = builder.accountReference;
        this.transactionDesc = builder.transactionDesc;
        this.callbackUrl = builder.callbackUrl;
    }

    public static Builder builder() {
        return new Builder();
    }

    public void validate() {
        requireNonBlank("StandingOrderRequest", "standingOrderName", standingOrderName,
                "standingOrderName (must be unique per customer)");
        requireNonBlank("StandingOrderRequest", "partyA", partyA, "partyA (the paying customer's phone number)");
        requireNonBlank("StandingOrderRequest", "amount", amount == null ? null : amount.toPlainString(),
                "amount (whole numbers only)");
        requireNonBlank("StandingOrderRequest", "startDate", startDate, "startDate (format yyyyMMdd)");
        requireNonBlank("StandingOrderRequest", "endDate", endDate, "endDate (format yyyyMMdd)");
        requireNonBlank("StandingOrderRequest", "frequency", frequency == null ? null : frequency.name(),
                "frequency (how often the order executes)");
        requireNonBlank("StandingOrderRequest", "accountReference", accountReference, "accountReference (up to 12 characters)");
    }

    public String toJson(Daraja4jConfig config) {
        validate();

        String resolvedShortCode = firstNonBlank(businessShortCode, config.getDefaultShortcode());
        String resolvedCallbackUrl = firstNonBlank(callbackUrl, config.getDefaultCallbackUrl());
        requireResolved("StandingOrderRequest", "businessShortCode", resolvedShortCode,
                "StandingOrderRequest.builder().businessShortCode(...) or Daraja4jConfig.builder().defaultShortcode(...)");
        requireResolved("StandingOrderRequest", "callbackUrl", resolvedCallbackUrl,
                "StandingOrderRequest.builder().callbackUrl(...) or Daraja4jConfig.builder().defaultCallbackUrl(...)");

        return new JsonWriter()
                .field("StandingOrderName", standingOrderName)
                .field("ReceiverPartyIdentifierType", transactionType.getReceiverPartyIdentifierType().getWireValue())
                .field("TransactionType", transactionType.getWireValue())
                .field("BusinessShortCode", resolvedShortCode)
                .field("PartyA", PhoneNormalizer.normalize(partyA))
                .field("Amount", amount.toPlainString())
                .field("StartDate", startDate)
                .field("EndDate", endDate)
                .field("Frequency", frequency.getWireValue())
                .field("CustomStoId", customStoId)
                .field("AccountReference", accountReference)
                .field("TransactionDesc", transactionDesc != null ? transactionDesc : accountReference)
                .field("CallBackURL", resolvedCallbackUrl)
                .build();
    }

    public String getStandingOrderName() {
        return standingOrderName;
    }

    public StandingOrderTransactionType getTransactionType() {
        return transactionType;
    }

    public String getBusinessShortCode() {
        return businessShortCode;
    }

    public String getPartyA() {
        return partyA;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public String getStartDate() {
        return startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public StandingOrderFrequency getFrequency() {
        return frequency;
    }

    public String getCustomStoId() {
        return customStoId;
    }

    public String getAccountReference() {
        return accountReference;
    }

    public String getTransactionDesc() {
        return transactionDesc;
    }

    public String getCallbackUrl() {
        return callbackUrl;
    }

    /** Builder for {@link StandingOrderRequest}. */
    public static final class Builder {
        private String standingOrderName;
        private StandingOrderTransactionType transactionType;
        private String businessShortCode;
        private String partyA;
        private BigDecimal amount;
        private String startDate;
        private String endDate;
        private StandingOrderFrequency frequency;
        private String customStoId;
        private String accountReference;
        private String transactionDesc;
        private String callbackUrl;

        private Builder() {
        }

        public Builder standingOrderName(String standingOrderName) {
            this.standingOrderName = standingOrderName;
            return this;
        }

        /** {@code PAY_BILL} (default) or {@code BUY_GOODS}. */
        public Builder transactionType(StandingOrderTransactionType transactionType) {
            this.transactionType = transactionType;
            return this;
        }

        /** Overrides {@link Daraja4jConfig#getDefaultShortcode()} for this request. */
        public Builder businessShortCode(String businessShortCode) {
            this.businessShortCode = businessShortCode;
            return this;
        }

        public Builder partyA(String partyA) {
            this.partyA = partyA;
            return this;
        }

        public Builder amount(BigDecimal amount) {
            this.amount = amount;
            return this;
        }

        public Builder amount(long amount) {
            this.amount = BigDecimal.valueOf(amount);
            return this;
        }

        /** Format {@code yyyyMMdd}. */
        public Builder startDate(String startDate) {
            this.startDate = startDate;
            return this;
        }

        /** Format {@code yyyyMMdd}. */
        public Builder endDate(String endDate) {
            this.endDate = endDate;
            return this;
        }

        public Builder frequency(StandingOrderFrequency frequency) {
            this.frequency = frequency;
            return this;
        }

        /** A tracking UUID for this request; a random one is generated if not set. */
        public Builder customStoId(String customStoId) {
            this.customStoId = customStoId;
            return this;
        }

        public Builder accountReference(String accountReference) {
            this.accountReference = accountReference;
            return this;
        }

        public Builder transactionDesc(String transactionDesc) {
            this.transactionDesc = transactionDesc;
            return this;
        }

        public Builder callbackUrl(String callbackUrl) {
            this.callbackUrl = callbackUrl;
            return this;
        }

        public StandingOrderRequest build() {
            return new StandingOrderRequest(this);
        }
    }
}
