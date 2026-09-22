package io.github.josemodi97.daraja4j.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

class StkCallbackResultTest {

    @Test
    void parse_successfulCallback_exposesMetadata() {
        // Verbatim sample from Safaricom's Daraja documentation.
        String json = "{\n"
                + "  \"Body\": {\n"
                + "    \"stkCallback\": {\n"
                + "      \"MerchantRequestID\": \"29115-34620561-1\",\n"
                + "      \"CheckoutRequestID\": \"ws_CO_191220191020363925\",\n"
                + "      \"ResultCode\": 0,\n"
                + "      \"ResultDesc\": \"The service request is processed successfully.\",\n"
                + "      \"CallbackMetadata\": {\n"
                + "        \"Item\": [\n"
                + "          {\"Name\": \"Amount\", \"Value\": 1.0},\n"
                + "          {\"Name\": \"MpesaReceiptNumber\", \"Value\": \"NLJ7RT61SV\"},\n"
                + "          {\"Name\": \"TransactionDate\", \"Value\": 20191219102115},\n"
                + "          {\"Name\": \"PhoneNumber\", \"Value\": 254708374149}\n"
                + "        ]\n"
                + "      }\n"
                + "    }\n"
                + "  }\n"
                + "}";

        StkCallbackResult result = StkCallbackResult.parse(json);

        assertTrue(result.isSuccess());
        assertEquals(0, result.getResultCode());
        assertEquals("NLJ7RT61SV", result.getMpesaReceiptNumber());
        assertEquals(new BigDecimal("1.0"), result.getAmount());
        assertEquals("254708374149", result.getPhoneNumber());
        assertEquals("20191219102115", result.getTransactionDate());
    }

    @Test
    void parse_cancelledCallback_hasNoMetadataAndIsNotSuccess() {
        String json = "{\n"
                + "  \"Body\": {\n"
                + "    \"stkCallback\": {\n"
                + "      \"MerchantRequestID\": \"f1e2-4b95-a71d-b30d3cdbb7a7942864\",\n"
                + "      \"CheckoutRequestID\": \"ws_CO_21072024125243250722943992\",\n"
                + "      \"ResultCode\": 1032,\n"
                + "      \"ResultDesc\": \"Request cancelled by user\"\n"
                + "    }\n"
                + "  }\n"
                + "}";

        StkCallbackResult result = StkCallbackResult.parse(json);

        assertFalse(result.isSuccess());
        assertEquals(1032, result.getResultCode());
        assertEquals("Request cancelled by user", result.getResultDesc());
        assertEquals(null, result.getMpesaReceiptNumber());
    }
}
