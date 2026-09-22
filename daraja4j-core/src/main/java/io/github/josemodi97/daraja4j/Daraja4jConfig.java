package io.github.josemodi97.daraja4j;

import io.github.josemodi97.daraja4j.exception.Daraja4jConfigurationException;

/**
 * Immutable connection settings for a {@link Daraja4jClient}: app
 * credentials, which Daraja environment to talk to, and defaults applied to
 * every request.
 *
 * <p>Build one with {@link #builder()}, or load it straight from environment
 * variables with {@link #fromEnvironment()} (useful for containers / CI where
 * credentials are injected rather than hardcoded).
 */
public final class Daraja4jConfig {

    /** Which Daraja host a client talks to. */
    public enum Environment {
        SANDBOX("https://sandbox.safaricom.co.ke"),
        PRODUCTION("https://api.safaricom.co.ke");

        private final String defaultBaseUrl;

        Environment(String defaultBaseUrl) {
            this.defaultBaseUrl = defaultBaseUrl;
        }

        public String getDefaultBaseUrl() {
            return defaultBaseUrl;
        }
    }

    private static final int DEFAULT_TIMEOUT_MILLIS = 30_000;
    private static final int DEFAULT_TOKEN_SAFETY_MARGIN_SECONDS = 60;

    private final String consumerKey;
    private final String consumerSecret;
    private final Environment environment;
    private final String baseUrl;
    private final String initiatorName;
    private final String securityCredential;
    private final String defaultShortcode;
    private final String defaultPasskey;
    private final String defaultCallbackUrl;
    private final String defaultResultUrl;
    private final String defaultQueueTimeoutUrl;
    private final String defaultValidationUrl;
    private final String defaultConfirmationUrl;
    private final int connectTimeoutMillis;
    private final int readTimeoutMillis;
    private final int tokenExpirySafetyMarginSeconds;

    private Daraja4jConfig(Builder builder) {
        this.consumerKey = trim(builder.consumerKey);
        this.consumerSecret = trim(builder.consumerSecret);
        this.environment = builder.environment;
        this.baseUrl = trim(builder.baseUrl);
        this.initiatorName = trim(builder.initiatorName);
        this.securityCredential = trim(builder.securityCredential);
        this.defaultShortcode = trim(builder.defaultShortcode);
        this.defaultPasskey = trim(builder.defaultPasskey);
        this.defaultCallbackUrl = trim(builder.defaultCallbackUrl);
        this.defaultResultUrl = trim(builder.defaultResultUrl);
        this.defaultQueueTimeoutUrl = trim(builder.defaultQueueTimeoutUrl);
        this.defaultValidationUrl = trim(builder.defaultValidationUrl);
        this.defaultConfirmationUrl = trim(builder.defaultConfirmationUrl);
        this.connectTimeoutMillis = builder.connectTimeoutMillis > 0 ? builder.connectTimeoutMillis : DEFAULT_TIMEOUT_MILLIS;
        this.readTimeoutMillis = builder.readTimeoutMillis > 0 ? builder.readTimeoutMillis : DEFAULT_TIMEOUT_MILLIS;
        this.tokenExpirySafetyMarginSeconds = builder.tokenExpirySafetyMarginSeconds > 0
                ? builder.tokenExpirySafetyMarginSeconds : DEFAULT_TOKEN_SAFETY_MARGIN_SECONDS;
    }

    /**
     * Reads {@code DARAJA4J_CONSUMER_KEY}, {@code DARAJA4J_CONSUMER_SECRET},
     * {@code DARAJA4J_ENVIRONMENT} ({@code SANDBOX} or {@code PRODUCTION}),
     * {@code DARAJA4J_BASE_URL}, {@code DARAJA4J_INITIATOR_NAME},
     * {@code DARAJA4J_SECURITY_CREDENTIAL}, {@code DARAJA4J_SHORTCODE},
     * {@code DARAJA4J_PASSKEY}, {@code DARAJA4J_CALLBACK_URL},
     * {@code DARAJA4J_RESULT_URL}, {@code DARAJA4J_QUEUE_TIMEOUT_URL},
     * {@code DARAJA4J_VALIDATION_URL}, {@code DARAJA4J_CONFIRMATION_URL},
     * {@code DARAJA4J_CONNECT_TIMEOUT_MILLIS} and
     * {@code DARAJA4J_READ_TIMEOUT_MILLIS} from the process environment.
     */
    public static Daraja4jConfig fromEnvironment() {
        Builder builder = builder()
                .consumerKey(System.getenv("DARAJA4J_CONSUMER_KEY"))
                .consumerSecret(System.getenv("DARAJA4J_CONSUMER_SECRET"))
                .baseUrl(System.getenv("DARAJA4J_BASE_URL"))
                .initiatorName(System.getenv("DARAJA4J_INITIATOR_NAME"))
                .securityCredential(System.getenv("DARAJA4J_SECURITY_CREDENTIAL"))
                .defaultShortcode(System.getenv("DARAJA4J_SHORTCODE"))
                .defaultPasskey(System.getenv("DARAJA4J_PASSKEY"))
                .defaultCallbackUrl(System.getenv("DARAJA4J_CALLBACK_URL"))
                .defaultResultUrl(System.getenv("DARAJA4J_RESULT_URL"))
                .defaultQueueTimeoutUrl(System.getenv("DARAJA4J_QUEUE_TIMEOUT_URL"))
                .defaultValidationUrl(System.getenv("DARAJA4J_VALIDATION_URL"))
                .defaultConfirmationUrl(System.getenv("DARAJA4J_CONFIRMATION_URL"));

        String environment = System.getenv("DARAJA4J_ENVIRONMENT");
        if (environment != null && !environment.trim().isEmpty()) {
            builder.environment(Environment.valueOf(environment.trim().toUpperCase(java.util.Locale.ROOT)));
        }
        String connectTimeout = System.getenv("DARAJA4J_CONNECT_TIMEOUT_MILLIS");
        if (connectTimeout != null && !connectTimeout.trim().isEmpty()) {
            builder.connectTimeoutMillis(Integer.parseInt(connectTimeout.trim()));
        }
        String readTimeout = System.getenv("DARAJA4J_READ_TIMEOUT_MILLIS");
        if (readTimeout != null && !readTimeout.trim().isEmpty()) {
            builder.readTimeoutMillis(Integer.parseInt(readTimeout.trim()));
        }

        return builder.build();
    }

    public static Builder builder() {
        return new Builder();
    }

    /** @throws Daraja4jConfigurationException if a required setting is missing */
    public void validate() {
        requireNonBlank("consumerKey", consumerKey);
        requireNonBlank("consumerSecret", consumerSecret);
        if (environment == null && (baseUrl == null || baseUrl.isEmpty())) {
            throw new Daraja4jConfigurationException(
                    "Daraja4jConfig is missing a required 'environment' setting. "
                            + "Set it via Daraja4jConfig.builder().environment(Daraja4jConfig.Environment.SANDBOX or .PRODUCTION), "
                            + "or the DARAJA4J_ENVIRONMENT environment variable, or override baseUrl(...) directly.");
        }
    }

    private static void requireNonBlank(String name, String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new Daraja4jConfigurationException(
                    "Daraja4jConfig is missing the required '" + name + "' setting. "
                            + "Set it via Daraja4jConfig.builder()." + name + "(...) or the matching "
                            + "DARAJA4J_* environment variable.");
        }
    }

    private static String trim(String value) {
        return value == null ? null : value.trim();
    }

    /** The base URL requests are sent to: the explicit override if set, otherwise the environment's default host. */
    public String getResolvedBaseUrl() {
        if (baseUrl != null && !baseUrl.isEmpty()) {
            return stripTrailingSlash(baseUrl);
        }
        if (environment != null) {
            return environment.getDefaultBaseUrl();
        }
        throw new Daraja4jConfigurationException(
                "Daraja4jConfig has neither an environment() nor a baseUrl() set - call validate() first.");
    }

    private static String stripTrailingSlash(String url) {
        return url.endsWith("/") ? url.substring(0, url.length() - 1) : url;
    }

    public String getConsumerKey() {
        return consumerKey;
    }

    public String getConsumerSecret() {
        return consumerSecret;
    }

    public Environment getEnvironment() {
        return environment;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public String getInitiatorName() {
        return initiatorName;
    }

    public String getSecurityCredential() {
        return securityCredential;
    }

    public String getDefaultShortcode() {
        return defaultShortcode;
    }

    public String getDefaultPasskey() {
        return defaultPasskey;
    }

    public String getDefaultCallbackUrl() {
        return defaultCallbackUrl;
    }

    public String getDefaultResultUrl() {
        return defaultResultUrl;
    }

    public String getDefaultQueueTimeoutUrl() {
        return defaultQueueTimeoutUrl;
    }

    public String getDefaultValidationUrl() {
        return defaultValidationUrl;
    }

    public String getDefaultConfirmationUrl() {
        return defaultConfirmationUrl;
    }

    public int getConnectTimeoutMillis() {
        return connectTimeoutMillis;
    }

    public int getReadTimeoutMillis() {
        return readTimeoutMillis;
    }

    public int getTokenExpirySafetyMarginSeconds() {
        return tokenExpirySafetyMarginSeconds;
    }

    /** Builder for {@link Daraja4jConfig}. */
    public static final class Builder {
        private String consumerKey;
        private String consumerSecret;
        private Environment environment;
        private String baseUrl;
        private String initiatorName;
        private String securityCredential;
        private String defaultShortcode;
        private String defaultPasskey;
        private String defaultCallbackUrl;
        private String defaultResultUrl;
        private String defaultQueueTimeoutUrl;
        private String defaultValidationUrl;
        private String defaultConfirmationUrl;
        private int connectTimeoutMillis;
        private int readTimeoutMillis;
        private int tokenExpirySafetyMarginSeconds;

        private Builder() {
        }

        public Builder consumerKey(String consumerKey) {
            this.consumerKey = consumerKey;
            return this;
        }

        public Builder consumerSecret(String consumerSecret) {
            this.consumerSecret = consumerSecret;
            return this;
        }

        public Builder environment(Environment environment) {
            this.environment = environment;
            return this;
        }

        /** Overrides the host derived from {@link #environment(Environment)} entirely - useful for testing against a stub server. */
        public Builder baseUrl(String baseUrl) {
            this.baseUrl = baseUrl;
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

        public Builder defaultShortcode(String defaultShortcode) {
            this.defaultShortcode = defaultShortcode;
            return this;
        }

        public Builder defaultPasskey(String defaultPasskey) {
            this.defaultPasskey = defaultPasskey;
            return this;
        }

        public Builder defaultCallbackUrl(String defaultCallbackUrl) {
            this.defaultCallbackUrl = defaultCallbackUrl;
            return this;
        }

        public Builder defaultResultUrl(String defaultResultUrl) {
            this.defaultResultUrl = defaultResultUrl;
            return this;
        }

        public Builder defaultQueueTimeoutUrl(String defaultQueueTimeoutUrl) {
            this.defaultQueueTimeoutUrl = defaultQueueTimeoutUrl;
            return this;
        }

        public Builder defaultValidationUrl(String defaultValidationUrl) {
            this.defaultValidationUrl = defaultValidationUrl;
            return this;
        }

        public Builder defaultConfirmationUrl(String defaultConfirmationUrl) {
            this.defaultConfirmationUrl = defaultConfirmationUrl;
            return this;
        }

        public Builder connectTimeoutMillis(int connectTimeoutMillis) {
            this.connectTimeoutMillis = connectTimeoutMillis;
            return this;
        }

        public Builder readTimeoutMillis(int readTimeoutMillis) {
            this.readTimeoutMillis = readTimeoutMillis;
            return this;
        }

        public Builder tokenExpirySafetyMarginSeconds(int tokenExpirySafetyMarginSeconds) {
            this.tokenExpirySafetyMarginSeconds = tokenExpirySafetyMarginSeconds;
            return this;
        }

        public Daraja4jConfig build() {
            return new Daraja4jConfig(this);
        }
    }
}
