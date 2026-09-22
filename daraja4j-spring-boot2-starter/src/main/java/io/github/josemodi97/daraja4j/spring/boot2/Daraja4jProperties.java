package io.github.josemodi97.daraja4j.spring.boot2;

import io.github.josemodi97.daraja4j.Daraja4jConfig;
import java.util.Locale;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Binds {@code daraja4j.*} entries in {@code application.yml}/{@code .properties}
 * to a {@link Daraja4jConfig}.
 *
 * <pre>{@code
 * daraja4j:
 *   consumer-key: ${DARAJA4J_CONSUMER_KEY}
 *   consumer-secret: ${DARAJA4J_CONSUMER_SECRET}
 *   environment: SANDBOX
 *   shortcode: "174379"
 *   passkey: ${DARAJA4J_PASSKEY}
 *   callback-url: https://yourapp.example.com/daraja4j/stk-callback
 *   webhook:
 *     stk:
 *       enabled: true
 * }</pre>
 */
@ConfigurationProperties(prefix = "daraja4j")
public class Daraja4jProperties {

    /** Daraja app consumer key. Required. */
    private String consumerKey;

    /** Daraja app consumer secret. Required. */
    private String consumerSecret;

    /** {@code SANDBOX} or {@code PRODUCTION}. Required unless {@link #baseUrl} is set. */
    private String environment;

    /** Overrides the host derived from {@link #environment}. Optional. */
    private String baseUrl;

    /** M-Pesa API operator username, for Initiator-authenticated operations (B2C, B2B, Reversal, etc.). */
    private String initiatorName;

    /** RSA-encrypted Initiator password - see {@code SecurityCredentialEncoder}. */
    private String securityCredential;

    /** Default shortcode for operations that don't set one explicitly. */
    private String shortcode;

    /** Default STK Push passkey. */
    private String passkey;

    /** Default STK Push callback URL. */
    private String callbackUrl;

    /** Default B2C/B2B/Reversal/Balance/StatusQuery result URL. */
    private String resultUrl;

    /** Default queue timeout URL. */
    private String queueTimeoutUrl;

    /** Default C2B validation URL. */
    private String validationUrl;

    /** Default C2B confirmation URL. */
    private String confirmationUrl;

    /** Connect timeout in milliseconds for outbound Daraja calls. */
    private int connectTimeoutMillis;

    /** Read timeout in milliseconds for outbound Daraja calls. */
    private int readTimeoutMillis;

    /** Safety margin (seconds) subtracted from the OAuth token's expiry before it's treated as expired. */
    private int tokenExpirySafetyMarginSeconds;

    private final Webhook webhook = new Webhook();

    public Daraja4jConfig toConfig() {
        Daraja4jConfig.Builder builder = Daraja4jConfig.builder()
                .consumerKey(consumerKey)
                .consumerSecret(consumerSecret)
                .initiatorName(initiatorName)
                .securityCredential(securityCredential)
                .defaultShortcode(shortcode)
                .defaultPasskey(passkey)
                .defaultCallbackUrl(callbackUrl)
                .defaultResultUrl(resultUrl)
                .defaultQueueTimeoutUrl(queueTimeoutUrl)
                .defaultValidationUrl(validationUrl)
                .defaultConfirmationUrl(confirmationUrl);

        if (environment != null && !environment.trim().isEmpty()) {
            builder.environment(Daraja4jConfig.Environment.valueOf(environment.trim().toUpperCase(Locale.ROOT)));
        }
        if (baseUrl != null && !baseUrl.trim().isEmpty()) {
            builder.baseUrl(baseUrl);
        }
        if (connectTimeoutMillis > 0) {
            builder.connectTimeoutMillis(connectTimeoutMillis);
        }
        if (readTimeoutMillis > 0) {
            builder.readTimeoutMillis(readTimeoutMillis);
        }
        if (tokenExpirySafetyMarginSeconds > 0) {
            builder.tokenExpirySafetyMarginSeconds(tokenExpirySafetyMarginSeconds);
        }

        return builder.build();
    }

    public String getConsumerKey() {
        return consumerKey;
    }

    public void setConsumerKey(String consumerKey) {
        this.consumerKey = consumerKey;
    }

    public String getConsumerSecret() {
        return consumerSecret;
    }

    public void setConsumerSecret(String consumerSecret) {
        this.consumerSecret = consumerSecret;
    }

    public String getEnvironment() {
        return environment;
    }

    public void setEnvironment(String environment) {
        this.environment = environment;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public String getInitiatorName() {
        return initiatorName;
    }

    public void setInitiatorName(String initiatorName) {
        this.initiatorName = initiatorName;
    }

    public String getSecurityCredential() {
        return securityCredential;
    }

    public void setSecurityCredential(String securityCredential) {
        this.securityCredential = securityCredential;
    }

    public String getShortcode() {
        return shortcode;
    }

    public void setShortcode(String shortcode) {
        this.shortcode = shortcode;
    }

    public String getPasskey() {
        return passkey;
    }

    public void setPasskey(String passkey) {
        this.passkey = passkey;
    }

    public String getCallbackUrl() {
        return callbackUrl;
    }

    public void setCallbackUrl(String callbackUrl) {
        this.callbackUrl = callbackUrl;
    }

    public String getResultUrl() {
        return resultUrl;
    }

    public void setResultUrl(String resultUrl) {
        this.resultUrl = resultUrl;
    }

    public String getQueueTimeoutUrl() {
        return queueTimeoutUrl;
    }

    public void setQueueTimeoutUrl(String queueTimeoutUrl) {
        this.queueTimeoutUrl = queueTimeoutUrl;
    }

    public String getValidationUrl() {
        return validationUrl;
    }

    public void setValidationUrl(String validationUrl) {
        this.validationUrl = validationUrl;
    }

    public String getConfirmationUrl() {
        return confirmationUrl;
    }

    public void setConfirmationUrl(String confirmationUrl) {
        this.confirmationUrl = confirmationUrl;
    }

    public int getConnectTimeoutMillis() {
        return connectTimeoutMillis;
    }

    public void setConnectTimeoutMillis(int connectTimeoutMillis) {
        this.connectTimeoutMillis = connectTimeoutMillis;
    }

    public int getReadTimeoutMillis() {
        return readTimeoutMillis;
    }

    public void setReadTimeoutMillis(int readTimeoutMillis) {
        this.readTimeoutMillis = readTimeoutMillis;
    }

    public int getTokenExpirySafetyMarginSeconds() {
        return tokenExpirySafetyMarginSeconds;
    }

    public void setTokenExpirySafetyMarginSeconds(int tokenExpirySafetyMarginSeconds) {
        this.tokenExpirySafetyMarginSeconds = tokenExpirySafetyMarginSeconds;
    }

    public Webhook getWebhook() {
        return webhook;
    }

    /** {@code daraja4j.webhook.*} - the three optional auto-registered callback endpoints. */
    public static class Webhook {

        private final Endpoint stk = new Endpoint("/daraja4j/stk-callback");
        private final Endpoint result = new Endpoint("/daraja4j/result-callback");
        private final Endpoint c2b = new Endpoint("/daraja4j/c2b-confirmation");

        /** {@code daraja4j.webhook.stk.*} - STK Push callback, publishes {@link Daraja4jStkPaymentVerifiedEvent}/{@link Daraja4jStkPaymentFailedEvent}. */
        public Endpoint getStk() {
            return stk;
        }

        /** {@code daraja4j.webhook.result.*} - B2C/B2B/B2Pochi/Reversal/Balance/StatusQuery result callback, publishes {@link Daraja4jResultVerifiedEvent}/{@link Daraja4jResultFailedEvent}. */
        public Endpoint getResult() {
            return result;
        }

        /** {@code daraja4j.webhook.c2b.*} - C2B confirmation callback, publishes {@link Daraja4jC2bPaymentReceivedEvent}. */
        public Endpoint getC2b() {
            return c2b;
        }
    }

    /** One auto-registered endpoint's {@code enabled}/{@code path} settings. */
    public static class Endpoint {

        private boolean enabled;
        private String path;

        Endpoint(String defaultPath) {
            this.path = defaultPath;
        }

        /** Off by default - each endpoint only registers once explicitly enabled. */
        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        public String getPath() {
            return path;
        }

        public void setPath(String path) {
            this.path = path;
        }
    }
}
