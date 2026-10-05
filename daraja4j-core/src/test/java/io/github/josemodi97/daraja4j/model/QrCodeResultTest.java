package io.github.josemodi97.daraja4j.model;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Base64;
import org.junit.jupiter.api.Test;

class QrCodeResultTest {

    private static final String SAMPLE_JSON = "{\n"
            + "  \"ResponseCode\": \"00\",\n"
            + "  \"ResponseDescription\": \"The service request is processed successfully.\",\n"
            + "  \"RequestID\": \"12345-67890\",\n"
            + "  \"QRCode\": \"aGVsbG8gd29ybGQ=\"\n"
            + "}";

    @Test
    void fromJson_parsesFieldsCorrectly() {
        QrCodeResult result = QrCodeResult.fromJson(SAMPLE_JSON);

        assertTrue(result.isSuccess());
        assertTrue(result.isAccepted());
        assertEquals("00", result.getResponseCode());
        assertEquals("The service request is processed successfully.", result.getResponseDescription());
        assertEquals("12345-67890", result.getRequestId());
        assertEquals("aGVsbG8gd29ybGQ=", result.getQrCode());
        assertArrayEquals("hello world".getBytes(), result.toPngBytes());
    }

    @Test
    void toPngBytes_whenQrCodeEmpty_returnsEmptyArray() {
        QrCodeResult result = new QrCodeResult("00", "ok", null, null);
        assertArrayEquals(new byte[0], result.toPngBytes());
    }
}
