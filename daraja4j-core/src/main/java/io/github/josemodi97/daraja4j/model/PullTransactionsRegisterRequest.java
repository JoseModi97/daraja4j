package io.github.josemodi97.daraja4j.model;

import static io.github.josemodi97.daraja4j.model.RequestValidation.firstNonBlank;
import static io.github.josemodi97.daraja4j.model.RequestValidation.requireNonBlank;
import static io.github.josemodi97.daraja4j.model.RequestValidation.requireResolved;

import io.github.josemodi97.daraja4j.Daraja4jConfig;
import io.github.josemodi97.daraja4j.internal.JsonWriter;

/**
 * One-time registration of a shortcode for the Pull Transactions API: lets
 * you later retrieve C2B transactions for a period with
 * {@link PullTransactionsQueryRequest}, even ones whose callback notification
 * was missed.
 */
public final class PullTransactionsRegisterRequest {

    private final String shortcode;
    private final String requestType;
    private final String nominatedNumber;
    private final String callbackUrl;

    private PullTransactionsRegisterRequest(Builder builder) {
        this.shortcode = builder.shortcode;
        this.requestType = builder.requestType != null ? builder.requestType : "Pull";
        this.nominatedNumber = builder.nominatedNumber;
        this.callbackUrl = builder.callbackUrl;
    }

    public static Builder builder() {
        return new Builder();
    }

    public void validate() {
        requireNonBlank("PullTransactionsRegisterRequest", "nominatedNumber", nominatedNumber,
                "nominatedNumber (the Safaricom MSISDN associated with this organization account)");
    }

    public String toJson(Daraja4jConfig config) {
        validate();

        String resolvedShortcode = firstNonBlank(shortcode, config.getDefaultShortcode());
        String resolvedCallbackUrl = firstNonBlank(callbackUrl, config.getDefaultCallbackUrl());
        requireResolved("PullTransactionsRegisterRequest", "shortcode", resolvedShortcode,
                "PullTransactionsRegisterRequest.builder().shortcode(...) or Daraja4jConfig.builder().defaultShortcode(...)");
        requireResolved("PullTransactionsRegisterRequest", "callbackUrl", resolvedCallbackUrl,
                "PullTransactionsRegisterRequest.builder().callbackUrl(...) or Daraja4jConfig.builder().defaultCallbackUrl(...)");

        return new JsonWriter()
                .field("ShortCode", resolvedShortcode)
                .field("RequestType", requestType)
                .field("NominatedNumber", nominatedNumber)
                .field("CallBackURL", resolvedCallbackUrl)
                .build();
    }

    public String getShortcode() {
        return shortcode;
    }

    public String getRequestType() {
        return requestType;
    }

    public String getNominatedNumber() {
        return nominatedNumber;
    }

    public String getCallbackUrl() {
        return callbackUrl;
    }

    /** Builder for {@link PullTransactionsRegisterRequest}. */
    public static final class Builder {
        private String shortcode;
        private String requestType;
        private String nominatedNumber;
        private String callbackUrl;

        private Builder() {
        }

        public Builder shortcode(String shortcode) {
            this.shortcode = shortcode;
            return this;
        }

        /** Defaults to {@code "Pull"}, the only documented value. */
        public Builder requestType(String requestType) {
            this.requestType = requestType;
            return this;
        }

        public Builder nominatedNumber(String nominatedNumber) {
            this.nominatedNumber = nominatedNumber;
            return this;
        }

        public Builder callbackUrl(String callbackUrl) {
            this.callbackUrl = callbackUrl;
            return this;
        }

        public PullTransactionsRegisterRequest build() {
            return new PullTransactionsRegisterRequest(this);
        }
    }
}
