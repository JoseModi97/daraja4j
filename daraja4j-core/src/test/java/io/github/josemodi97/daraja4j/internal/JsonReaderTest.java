package io.github.josemodi97.daraja4j.internal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.github.josemodi97.daraja4j.exception.Daraja4jApiException;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

class JsonReaderTest {

    @Test
    void parseObject_flatObject_readsAllTypes() {
        Map<String, Object> body = JsonReader.parseObject(
                "{\"ResponseCode\":\"0\",\"Amount\":500,\"Flag\":true,\"Missing\":null}");

        assertEquals("0", JsonReader.getString(body, "ResponseCode"));
        assertEquals(500, JsonReader.getInt(body, "Amount"));
        assertEquals(Boolean.TRUE, body.get("Flag"));
        assertNull(body.get("Missing"));
    }

    @Test
    void parseObject_stkSuccessCallback_parsesNestedObjectAndArray() {
        // Verbatim sample from Safaricom's Daraja documentation for the
        // successful M-Pesa Express callback payload.
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

        Map<String, Object> root = JsonReader.parseObject(json);
        Map<String, Object> callback = JsonReader.getObject(JsonReader.getObject(root, "Body"), "stkCallback");

        assertEquals("ws_CO_191220191020363925", JsonReader.getString(callback, "CheckoutRequestID"));
        assertEquals(0, JsonReader.getInt(callback, "ResultCode"));

        List<Object> items = JsonReader.getArray(JsonReader.getObject(callback, "CallbackMetadata"), "Item");
        assertEquals(4, items.size());
        @SuppressWarnings("unchecked")
        Map<String, Object> receiptItem = (Map<String, Object>) items.get(1);
        assertEquals("MpesaReceiptNumber", receiptItem.get("Name"));
        assertEquals("NLJ7RT61SV", receiptItem.get("Value"));
    }

    @Test
    void parseObject_escapedString_unescapesCorrectly() {
        Map<String, Object> body = JsonReader.parseObject("{\"Text\":\"line1\\nline2\\t\\\"quoted\\\"\"}");
        assertEquals("line1\nline2\t\"quoted\"", JsonReader.getString(body, "Text"));
    }

    @Test
    void parseObject_malformedJson_throwsApiException() {
        assertThrows(Daraja4jApiException.class, () -> JsonReader.parseObject("{not valid json"));
    }

    @Test
    void parseObject_topLevelArray_throwsApiException() {
        assertThrows(Daraja4jApiException.class, () -> JsonReader.parseObject("[1,2,3]"));
    }

    @Test
    void getString_stringifiesNonStringValues() {
        Map<String, Object> body = JsonReader.parseObject("{\"Code\":0}");
        assertEquals("0", JsonReader.getString(body, "Code"));
    }

    @Test
    void parse_emptyObjectAndArray_areHandled() {
        Object emptyObject = JsonReader.parse("{}");
        Object emptyArray = JsonReader.parse("[]");
        assertTrue(((Map<?, ?>) emptyObject).isEmpty());
        assertTrue(((List<?>) emptyArray).isEmpty());
    }
}
