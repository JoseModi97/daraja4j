package io.github.josemodi97.daraja4j.model;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.github.josemodi97.daraja4j.Daraja4jConfig;
import io.github.josemodi97.daraja4j.exception.Daraja4jValidationException;
import org.junit.jupiter.api.Test;

class StkPushRequestTest {

    private static final Daraja4jConfig CONFIG_WITH_DEFAULTS = Daraja4jConfig.builder()
            .consumerKey("key")
            .consumerSecret("secret")
            .environment(Daraja4jConfig.Environment.SANDBOX)
            .defaultShortcode("174379")
            .defaultPasskey("bfb279f9aa9bdbcf158e97dd71a467cd2e0c893059b10f78e6b72ada1ed2c919")
            .defaultCallbackUrl("https://example.com/callback")
            .build();

    @Test
    void validate_missingAmount_throwsValidationException() {
        StkPushRequest request = StkPushRequest.builder()
                .partyA("0712345678")
                .accountReference("INV-0001")
                .build();

        assertThrows(Daraja4jValidationException.class, request::validate);
    }

    @Test
    void toJson_missingShortcodeAndConfigHasNoDefault_throwsValidationException() {
        Daraja4jConfig configWithoutDefaults = Daraja4jConfig.builder()
                .consumerKey("key")
                .consumerSecret("secret")
                .environment(Daraja4jConfig.Environment.SANDBOX)
                .build();

        StkPushRequest request = StkPushRequest.builder()
                .amount(500)
                .partyA("0712345678")
                .accountReference("INV-0001")
                .build();

        assertThrows(Daraja4jValidationException.class, () -> request.toJson(configWithoutDefaults));
    }

    @Test
    void toJson_normalizesPhoneAndDefaultsPartyBToShortcode() {
        StkPushRequest request = StkPushRequest.builder()
                .amount(500)
                .partyA("0712345678")
                .accountReference("INV-0001")
                .build();

        String json = request.toJson(CONFIG_WITH_DEFAULTS);

        assertTrue(json.contains("\"PartyA\":\"254712345678\""));
        assertTrue(json.contains("\"PhoneNumber\":\"254712345678\""));
        assertTrue(json.contains("\"PartyB\":\"174379\""));
        assertTrue(json.contains("\"BusinessShortCode\":\"174379\""));
        assertTrue(json.contains("\"TransactionType\":\"CustomerPayBillOnline\""));
    }

    @Test
    void toJson_perRequestShortcodeOverridesConfigDefault() {
        StkPushRequest request = StkPushRequest.builder()
                .amount(500)
                .partyA("0712345678")
                .accountReference("INV-0001")
                .shortcode("600999")
                .build();

        String json = request.toJson(CONFIG_WITH_DEFAULTS);

        assertTrue(json.contains("\"BusinessShortCode\":\"600999\""));
    }
}
