package io.github.josemodi97.daraja4j.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class Daraja4jResultCallbackTest {

    @Test
    void parse_successfulB2cResult_exposesResultParameters() {
        // Verbatim sample from Safaricom's Daraja documentation for a B2C result callback.
        String json = "{\n"
                + "  \"Result\": {\n"
                + "    \"ResultType\": 0,\n"
                + "    \"ResultCode\": 0,\n"
                + "    \"ResultDesc\": \"The service request is processed successfully.\",\n"
                + "    \"OriginatorConversationID\": \"53e3-4aa8-9fe0-8fb5e4092cdd3533373\",\n"
                + "    \"ConversationID\": \"AG_20240706_2010364430d9bbdaf872\",\n"
                + "    \"TransactionID\": \"SG632NMUAB\",\n"
                + "    \"ResultParameters\": {\n"
                + "      \"ResultParameter\": [\n"
                + "        {\"Key\": \"TransactionAmount\", \"Value\": 10},\n"
                + "        {\"Key\": \"TransactionReceipt\", \"Value\": \"SG632NMUAB\"},\n"
                + "        {\"Key\": \"ReceiverPartyPublicName\", \"Value\": \"254705912645 - NICHOLAS JOHN SONGOK\"}\n"
                + "      ]\n"
                + "    }\n"
                + "  }\n"
                + "}";

        Daraja4jResultCallback callback = Daraja4jResultCallback.parse(json);

        assertTrue(callback.isSuccess());
        assertEquals("SG632NMUAB", callback.getTransactionId());
        assertEquals("AG_20240706_2010364430d9bbdaf872", callback.getConversationId());
        assertEquals("SG632NMUAB", callback.getResultParameter("TransactionReceipt"));
        assertEquals(10L, callback.getResultParameter("TransactionAmount"));
    }

    @Test
    void parse_unsuccessfulReversalResult_hasAlphanumericResultCode() {
        // Verbatim sample from Safaricom's Daraja documentation for a failed reversal callback.
        String json = "{\n"
                + "  \"Result\": {\n"
                + "    \"ResultType\": 0,\n"
                + "    \"ResultCode\": \"R000002\",\n"
                + "    \"ResultDesc\": \"The OriginalTransactionID is invalid.\",\n"
                + "    \"OriginatorConversationID\": \"3124-481d-b706-10bdd6fbc8e21792398\",\n"
                + "    \"ConversationID\": \"AG_20211114_2010573069aefb6b625a\",\n"
                + "    \"TransactionID\": \"SKE0000000\"\n"
                + "  }\n"
                + "}";

        Daraja4jResultCallback callback = Daraja4jResultCallback.parse(json);

        assertFalse(callback.isSuccess());
        assertEquals("R000002", callback.getResultCode());
        assertEquals("The OriginalTransactionID is invalid.", callback.getResultDesc());
    }
}
