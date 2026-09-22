package io.github.josemodi97.daraja4j.model;

import static io.github.josemodi97.daraja4j.model.RequestValidation.firstNonBlank;
import static io.github.josemodi97.daraja4j.model.RequestValidation.requireNonBlank;
import static io.github.josemodi97.daraja4j.model.RequestValidation.requireResolved;

import io.github.josemodi97.daraja4j.Daraja4jConfig;
import io.github.josemodi97.daraja4j.internal.JsonWriter;
import io.github.josemodi97.daraja4j.util.PhoneNormalizer;
import io.github.josemodi97.daraja4j.util.StkCredentialsGenerator;
import java.math.BigDecimal;

/**
 * An M-Pesa Express ("STK Push") request: prompts the payer's phone for
 * their M-Pesa PIN to authorize a payment. POST to Daraja returns only an
 * acknowledgement ({@link StkPushResult}); the actual outcome arrives later
 * at {@code callbackUrl}, or can be polled with a {@link StkPushQueryRequest}.
 *
 * <p>Build one with {@link #builder()}. Only {@code amount}, {@code partyA}
 * and {@code accountReference} must be set on the request itself;
 * {@code shortcode}, {@code passkey} and {@code callbackUrl} fall back to
 * {@link Daraja4jConfig}'s defaults when not set here.
 */
public final class StkPushRequest {

    private final BigDecimal amount;
    private final String partyA;
    private final String phoneNumber;
    private final String accountReference;
    private final String transactionDesc;
    private final String callbackUrl;
    private final String shortcode;
    private final String passkey;
    private final C2bTransactionType transactionType;

    private StkPushRequest(Builder builder) {
        this.amount = builder.amount;
        this.partyA = builder.partyA;
        this.phoneNumber = builder.phoneNumber;
        this.accountReference = builder.accountReference;
        this.transactionDesc = builder.transactionDesc;
        this.callbackUrl = builder.callbackUrl;
        this.shortcode = builder.shortcode;
        this.passkey = builder.passkey;
        this.transactionType = builder.transactionType != null ? builder.transactionType : C2bTransactionType.CUSTOMER_PAY_BILL_ONLINE;
    }

    public static Builder builder() {
        return new Builder();
    }

    /** @throws io.github.josemodi97.daraja4j.exception.Daraja4jValidationException if a field owned by this request is missing */
    public void validate() {
        requireNonBlank("StkPushRequest", "amount", amount == null ? null : amount.toPlainString(),
                "amount (how much to charge, e.g. 500)");
        requireNonBlank("StkPushRequest", "partyA", partyA, "partyA (the payer's phone number, e.g. '0712345678')");
        requireNonBlank("StkPushRequest", "accountReference", accountReference,
                "accountReference (shown to the payer in the USSD prompt, max 12 chars)");
    }

    /** Builds the JSON body, resolving {@code shortcode}/{@code passkey}/{@code callbackUrl} from {@code config} when unset here. */
    public String toJson(Daraja4jConfig config) {
        validate();

        String resolvedShortcode = firstNonBlank(shortcode, config.getDefaultShortcode());
        String resolvedPasskey = firstNonBlank(passkey, config.getDefaultPasskey());
        String resolvedCallbackUrl = firstNonBlank(callbackUrl, config.getDefaultCallbackUrl());
        requireResolved("StkPushRequest", "shortcode", resolvedShortcode,
                "StkPushRequest.builder().shortcode(...) or Daraja4jConfig.builder().defaultShortcode(...)");
        requireResolved("StkPushRequest", "passkey", resolvedPasskey,
                "StkPushRequest.builder().passkey(...) or Daraja4jConfig.builder().defaultPasskey(...)");
        requireResolved("StkPushRequest", "callbackUrl", resolvedCallbackUrl,
                "StkPushRequest.builder().callbackUrl(...) or Daraja4jConfig.builder().defaultCallbackUrl(...)");

        String normalizedPartyA = PhoneNormalizer.normalize(partyA);
        String normalizedPhoneNumber = phoneNumber != null ? PhoneNormalizer.normalize(phoneNumber) : normalizedPartyA;
        String timestamp = StkCredentialsGenerator.timestamp();
        String password = StkCredentialsGenerator.password(resolvedShortcode, resolvedPasskey, timestamp);

        return new JsonWriter()
                .field("BusinessShortCode", resolvedShortcode)
                .field("Password", password)
                .field("Timestamp", timestamp)
                .field("TransactionType", transactionType.getWireValue())
                .field("Amount", amount.toPlainString())
                .field("PartyA", normalizedPartyA)
                .field("PartyB", resolvedShortcode)
                .field("PhoneNumber", normalizedPhoneNumber)
                .field("CallBackURL", resolvedCallbackUrl)
                .field("AccountReference", accountReference)
                .field("TransactionDesc", transactionDesc != null ? transactionDesc : accountReference)
                .build();
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public String getPartyA() {
        return partyA;
    }

    public String getPhoneNumber() {
        return phoneNumber;
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

    public String getShortcode() {
        return shortcode;
    }

    public String getPasskey() {
        return passkey;
    }

    public C2bTransactionType getTransactionType() {
        return transactionType;
    }

    /** Builder for {@link StkPushRequest}. */
    public static final class Builder {
        private BigDecimal amount;
        private String partyA;
        private String phoneNumber;
        private String accountReference;
        private String transactionDesc;
        private String callbackUrl;
        private String shortcode;
        private String passkey;
        private C2bTransactionType transactionType;

        private Builder() {
        }

        public Builder amount(BigDecimal amount) {
            this.amount = amount;
            return this;
        }

        public Builder amount(double amount) {
            this.amount = BigDecimal.valueOf(amount);
            return this;
        }

        public Builder amount(String amount) {
            this.amount = (amount == null || amount.trim().isEmpty()) ? null : new BigDecimal(amount.trim());
            return this;
        }

        /** The payer's phone number. Accepts any of the formats {@link PhoneNormalizer#normalize(String)} handles. */
        public Builder partyA(String partyA) {
            this.partyA = partyA;
            return this;
        }

        /** The number to receive the USSD prompt, if different from {@link #partyA(String)}. Defaults to {@code partyA} when unset. */
        public Builder phoneNumber(String phoneNumber) {
            this.phoneNumber = phoneNumber;
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

        /** Overrides {@link Daraja4jConfig#getDefaultShortcode()} for this request. */
        public Builder shortcode(String shortcode) {
            this.shortcode = shortcode;
            return this;
        }

        /** Overrides {@link Daraja4jConfig#getDefaultPasskey()} for this request. */
        public Builder passkey(String passkey) {
            this.passkey = passkey;
            return this;
        }

        /** {@code CustomerPayBillOnline} (default) or {@code CustomerBuyGoodsOnline}. */
        public Builder transactionType(C2bTransactionType transactionType) {
            this.transactionType = transactionType;
            return this;
        }

        public StkPushRequest build() {
            return new StkPushRequest(this);
        }
    }
}
