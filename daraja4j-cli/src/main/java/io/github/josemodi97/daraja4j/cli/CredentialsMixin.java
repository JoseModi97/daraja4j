package io.github.josemodi97.daraja4j.cli;

import io.github.josemodi97.daraja4j.Daraja4jConfig;
import java.util.Locale;
import picocli.CommandLine.Option;

/**
 * Shared connection flags for every subcommand. Any flag left unset falls
 * back to its {@code DARAJA4J_*} environment variable (see
 * {@link Daraja4jConfig#fromEnvironment()}), so CI/CD pipelines can omit
 * them entirely and rely on injected env vars.
 */
public final class CredentialsMixin {

    @Option(names = "--consumer-key", description = "Daraja consumer key (or DARAJA4J_CONSUMER_KEY)")
    String consumerKey;

    @Option(names = "--consumer-secret", description = "Daraja consumer secret (or DARAJA4J_CONSUMER_SECRET)")
    String consumerSecret;

    @Option(names = "--environment", description = "SANDBOX or PRODUCTION (or DARAJA4J_ENVIRONMENT)")
    String environment;

    @Option(names = "--base-url", description = "Override the Daraja host entirely (or DARAJA4J_BASE_URL)")
    String baseUrl;

    @Option(names = "--initiator-name", description = "Initiator username, for B2C/B2B/Reversal/Balance/StatusQuery (or DARAJA4J_INITIATOR_NAME)")
    String initiatorName;

    @Option(names = "--security-credential", description = "Encrypted Initiator password (or DARAJA4J_SECURITY_CREDENTIAL)")
    String securityCredential;

    @Option(names = "--shortcode", description = "Default shortcode (or DARAJA4J_SHORTCODE)")
    String shortcode;

    @Option(names = "--passkey", description = "Default STK Push passkey (or DARAJA4J_PASSKEY)")
    String passkey;

    @Option(names = "--callback-url", description = "Default STK Push callback URL (or DARAJA4J_CALLBACK_URL)")
    String callbackUrl;

    @Option(names = "--result-url", description = "Default result URL (or DARAJA4J_RESULT_URL)")
    String resultUrl;

    @Option(names = "--queue-timeout-url", description = "Default queue timeout URL (or DARAJA4J_QUEUE_TIMEOUT_URL)")
    String queueTimeoutUrl;

    public Daraja4jConfig toConfig() {
        Daraja4jConfig fromEnv = Daraja4jConfig.fromEnvironment();

        Daraja4jConfig.Builder builder = Daraja4jConfig.builder()
                .consumerKey(firstNonBlank(consumerKey, fromEnv.getConsumerKey()))
                .consumerSecret(firstNonBlank(consumerSecret, fromEnv.getConsumerSecret()))
                .baseUrl(firstNonBlank(baseUrl, fromEnv.getBaseUrl()))
                .initiatorName(firstNonBlank(initiatorName, fromEnv.getInitiatorName()))
                .securityCredential(firstNonBlank(securityCredential, fromEnv.getSecurityCredential()))
                .defaultShortcode(firstNonBlank(shortcode, fromEnv.getDefaultShortcode()))
                .defaultPasskey(firstNonBlank(passkey, fromEnv.getDefaultPasskey()))
                .defaultCallbackUrl(firstNonBlank(callbackUrl, fromEnv.getDefaultCallbackUrl()))
                .defaultResultUrl(firstNonBlank(resultUrl, fromEnv.getDefaultResultUrl()))
                .defaultQueueTimeoutUrl(firstNonBlank(queueTimeoutUrl, fromEnv.getDefaultQueueTimeoutUrl()));

        String resolvedEnvironment = firstNonBlank(environment,
                fromEnv.getEnvironment() == null ? null : fromEnv.getEnvironment().name());
        if (resolvedEnvironment != null) {
            builder.environment(Daraja4jConfig.Environment.valueOf(resolvedEnvironment.trim().toUpperCase(Locale.ROOT)));
        }

        return builder.build();
    }

    private static String firstNonBlank(String flagValue, String envValue) {
        return (flagValue != null && !flagValue.trim().isEmpty()) ? flagValue : envValue;
    }
}
