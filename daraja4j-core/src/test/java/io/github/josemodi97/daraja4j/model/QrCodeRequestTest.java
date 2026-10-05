package io.github.josemodi97.daraja4j.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.github.josemodi97.daraja4j.Daraja4jConfig;
import io.github.josemodi97.daraja4j.exception.Daraja4jValidationException;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

class QrCodeRequestTest {

    private static final Daraja4jConfig CONFIG = Daraja4jConfig.builder()
            .consumerKey("key")
            .consumerSecret("secret")
            .environment(Daraja4jConfig.Environment.SANDBOX)
            .defaultShortcode("174379")
            .build();

    @Test
    void toJson_withDefaultCpi_serializesValidJson() {
        QrCodeRequest request = QrCodeRequest.builder()
                .merchantName("Coffee Shop")
                .refNo("INV-100")
                .amount(250)
                .trxCode(QrTransactionType.BUY_GOODS)
                .size(400)
                .build();

        String json = request.toJson(CONFIG);

        assertTrue(json.contains("\"MerchantName\":\"Coffee Shop\""));
        assertTrue(json.contains("\"RefNo\":\"INV-100\""));
        assertTrue(json.contains("\"Amount\":250"));
        assertTrue(json.contains("\"TrxCode\":\"BG\""));
        assertTrue(json.contains("\"CPI\":\"174379\""));
        assertTrue(json.contains("\"Size\":\"400\""));
    }

    @Test
    void toJson_withExplicitCpiAndDefaultType_serializesPaybill() {
        QrCodeRequest request = QrCodeRequest.builder()
                .merchantName("School Admin")
                .refNo("STU-001")
                .amount(BigDecimal.valueOf(1500))
                .cpi("600980")
                .build();

        String json = request.toJson(CONFIG);

        assertTrue(json.contains("\"MerchantName\":\"School Admin\""));
        assertTrue(json.contains("\"TrxCode\":\"PB\""));
        assertTrue(json.contains("\"CPI\":\"600980\""));
        assertTrue(json.contains("\"Size\":\"300\""));
    }

    @Test
    void validate_missingMerchantName_throwsValidationException() {
        QrCodeRequest request = QrCodeRequest.builder()
                .refNo("REF-01")
                .amount(10)
                .build();

        assertThrows(Daraja4jValidationException.class, request::validate);
    }

    @Test
    void validate_missingAmount_throwsValidationException() {
        QrCodeRequest request = QrCodeRequest.builder()
                .merchantName("Store")
                .refNo("REF-01")
                .build();

        assertThrows(Daraja4jValidationException.class, request::validate);
    }
}
