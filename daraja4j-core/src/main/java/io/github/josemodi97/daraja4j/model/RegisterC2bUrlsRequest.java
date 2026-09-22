package io.github.josemodi97.daraja4j.model;

import static io.github.josemodi97.daraja4j.model.RequestValidation.firstNonBlank;
import static io.github.josemodi97.daraja4j.model.RequestValidation.requireNonBlank;
import static io.github.josemodi97.daraja4j.model.RequestValidation.requireResolved;

import io.github.josemodi97.daraja4j.Daraja4jConfig;
import io.github.josemodi97.daraja4j.internal.JsonWriter;

/**
 * Registers the URLs Daraja calls when a C2B payment lands on
 * {@code shortcode}: {@code validationUrl} (optional pre-payment check,
 * only invoked if External Validation is enabled on the shortcode) and
 * {@code confirmationUrl} (payment notification, always invoked). This is a
 * one-time call in production - see the project README before re-registering.
 */
public final class RegisterC2bUrlsRequest {

    /** What Daraja does with the transaction if {@code validationUrl} can't be reached in time. */
    public enum ResponseType {
        COMPLETED,
        CANCELLED
    }

    private final String shortcode;
    private final String confirmationUrl;
    private final String validationUrl;
    private final ResponseType responseType;

    private RegisterC2bUrlsRequest(Builder builder) {
        this.shortcode = builder.shortcode;
        this.confirmationUrl = builder.confirmationUrl;
        this.validationUrl = builder.validationUrl;
        this.responseType = builder.responseType != null ? builder.responseType : ResponseType.COMPLETED;
    }

    public static Builder builder() {
        return new Builder();
    }

    public void validate() {
        requireNonBlank("RegisterC2bUrlsRequest", "confirmationUrl", confirmationUrl,
                "confirmationUrl (where Daraja posts completed C2B payment notifications)");
        requireNonBlank("RegisterC2bUrlsRequest", "validationUrl", validationUrl,
                "validationUrl (required by Daraja even if External Validation is disabled on this shortcode)");
    }

    public String toJson(Daraja4jConfig config) {
        validate();

        String resolvedShortcode = firstNonBlank(shortcode, config.getDefaultShortcode());
        requireResolved("RegisterC2bUrlsRequest", "shortcode", resolvedShortcode,
                "RegisterC2bUrlsRequest.builder().shortcode(...) or Daraja4jConfig.builder().defaultShortcode(...)");

        return new JsonWriter()
                .field("ShortCode", resolvedShortcode)
                .field("ResponseType", responseType.name().charAt(0) + responseType.name().substring(1).toLowerCase(java.util.Locale.ROOT))
                .field("ConfirmationURL", confirmationUrl)
                .field("ValidationURL", validationUrl)
                .build();
    }

    public String getShortcode() {
        return shortcode;
    }

    public String getConfirmationUrl() {
        return confirmationUrl;
    }

    public String getValidationUrl() {
        return validationUrl;
    }

    public ResponseType getResponseType() {
        return responseType;
    }

    /** Builder for {@link RegisterC2bUrlsRequest}. */
    public static final class Builder {
        private String shortcode;
        private String confirmationUrl;
        private String validationUrl;
        private ResponseType responseType;

        private Builder() {
        }

        public Builder shortcode(String shortcode) {
            this.shortcode = shortcode;
            return this;
        }

        public Builder confirmationUrl(String confirmationUrl) {
            this.confirmationUrl = confirmationUrl;
            return this;
        }

        public Builder validationUrl(String validationUrl) {
            this.validationUrl = validationUrl;
            return this;
        }

        /** {@code COMPLETED} (default) or {@code CANCELLED} - what Daraja does if {@code validationUrl} is unreachable. */
        public Builder responseType(ResponseType responseType) {
            this.responseType = responseType;
            return this;
        }

        public RegisterC2bUrlsRequest build() {
            return new RegisterC2bUrlsRequest(this);
        }
    }
}
