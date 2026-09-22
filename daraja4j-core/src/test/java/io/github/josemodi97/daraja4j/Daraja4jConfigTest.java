package io.github.josemodi97.daraja4j;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import io.github.josemodi97.daraja4j.exception.Daraja4jConfigurationException;
import org.junit.jupiter.api.Test;

class Daraja4jConfigTest {

    @Test
    void validate_missingConsumerKey_throwsConfigurationException() {
        Daraja4jConfig config = Daraja4jConfig.builder()
                .consumerSecret("secret")
                .environment(Daraja4jConfig.Environment.SANDBOX)
                .build();

        assertThrows(Daraja4jConfigurationException.class, config::validate);
    }

    @Test
    void validate_missingEnvironmentAndBaseUrl_throwsConfigurationException() {
        Daraja4jConfig config = Daraja4jConfig.builder()
                .consumerKey("key")
                .consumerSecret("secret")
                .build();

        assertThrows(Daraja4jConfigurationException.class, config::validate);
    }

    @Test
    void validate_baseUrlOverrideWithoutEnvironment_isSufficient() {
        Daraja4jConfig config = Daraja4jConfig.builder()
                .consumerKey("key")
                .consumerSecret("secret")
                .baseUrl("https://stub.local")
                .build();

        config.validate();
    }

    @Test
    void getResolvedBaseUrl_sandboxEnvironment_returnsSandboxHost() {
        Daraja4jConfig config = Daraja4jConfig.builder()
                .consumerKey("key")
                .consumerSecret("secret")
                .environment(Daraja4jConfig.Environment.SANDBOX)
                .build();

        assertEquals("https://sandbox.safaricom.co.ke", config.getResolvedBaseUrl());
    }

    @Test
    void getResolvedBaseUrl_productionEnvironment_returnsProductionHost() {
        Daraja4jConfig config = Daraja4jConfig.builder()
                .consumerKey("key")
                .consumerSecret("secret")
                .environment(Daraja4jConfig.Environment.PRODUCTION)
                .build();

        assertEquals("https://api.safaricom.co.ke", config.getResolvedBaseUrl());
    }

    @Test
    void getResolvedBaseUrl_explicitOverride_takesPrecedenceAndStripsTrailingSlash() {
        Daraja4jConfig config = Daraja4jConfig.builder()
                .consumerKey("key")
                .consumerSecret("secret")
                .environment(Daraja4jConfig.Environment.SANDBOX)
                .baseUrl("https://stub.local/")
                .build();

        assertEquals("https://stub.local", config.getResolvedBaseUrl());
    }
}
