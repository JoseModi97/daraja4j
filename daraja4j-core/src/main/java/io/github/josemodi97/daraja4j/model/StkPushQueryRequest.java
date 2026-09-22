package io.github.josemodi97.daraja4j.model;

import static io.github.josemodi97.daraja4j.model.RequestValidation.firstNonBlank;
import static io.github.josemodi97.daraja4j.model.RequestValidation.requireNonBlank;
import static io.github.josemodi97.daraja4j.model.RequestValidation.requireResolved;

import io.github.josemodi97.daraja4j.Daraja4jConfig;
import io.github.josemodi97.daraja4j.internal.JsonWriter;
import io.github.josemodi97.daraja4j.util.StkCredentialsGenerator;

/**
 * Polls the outcome of a previously-submitted {@link StkPushRequest} by its
 * {@code CheckoutRequestID} - useful when the callback was missed, or as a
 * secondary reconciliation check before crediting an account.
 */
public final class StkPushQueryRequest {

    private final String checkoutRequestId;
    private final String shortcode;
    private final String passkey;

    private StkPushQueryRequest(Builder builder) {
        this.checkoutRequestId = builder.checkoutRequestId;
        this.shortcode = builder.shortcode;
        this.passkey = builder.passkey;
    }

    public static Builder builder() {
        return new Builder();
    }

    public void validate() {
        requireNonBlank("StkPushQueryRequest", "checkoutRequestId", checkoutRequestId,
                "checkoutRequestId (from the StkPushResult returned by stkPush(...))");
    }

    public String toJson(Daraja4jConfig config) {
        validate();

        String resolvedShortcode = firstNonBlank(shortcode, config.getDefaultShortcode());
        String resolvedPasskey = firstNonBlank(passkey, config.getDefaultPasskey());
        requireResolved("StkPushQueryRequest", "shortcode", resolvedShortcode,
                "StkPushQueryRequest.builder().shortcode(...) or Daraja4jConfig.builder().defaultShortcode(...)");
        requireResolved("StkPushQueryRequest", "passkey", resolvedPasskey,
                "StkPushQueryRequest.builder().passkey(...) or Daraja4jConfig.builder().defaultPasskey(...)");

        String timestamp = StkCredentialsGenerator.timestamp();
        String password = StkCredentialsGenerator.password(resolvedShortcode, resolvedPasskey, timestamp);

        return new JsonWriter()
                .field("BusinessShortCode", resolvedShortcode)
                .field("Password", password)
                .field("Timestamp", timestamp)
                .field("CheckoutRequestID", checkoutRequestId)
                .build();
    }

    public String getCheckoutRequestId() {
        return checkoutRequestId;
    }

    public String getShortcode() {
        return shortcode;
    }

    public String getPasskey() {
        return passkey;
    }

    /** Builder for {@link StkPushQueryRequest}. */
    public static final class Builder {
        private String checkoutRequestId;
        private String shortcode;
        private String passkey;

        private Builder() {
        }

        public Builder checkoutRequestId(String checkoutRequestId) {
            this.checkoutRequestId = checkoutRequestId;
            return this;
        }

        public Builder shortcode(String shortcode) {
            this.shortcode = shortcode;
            return this;
        }

        public Builder passkey(String passkey) {
            this.passkey = passkey;
            return this;
        }

        public StkPushQueryRequest build() {
            return new StkPushQueryRequest(this);
        }
    }
}
