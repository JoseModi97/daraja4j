package io.github.josemodi97.daraja4j.model;

import io.github.josemodi97.daraja4j.internal.JsonReader;
import java.util.Map;

/** Daraja's synchronous acknowledgement of a {@link B2PochiRequest}. The actual payment outcome arrives at {@code resultUrl}. */
public final class B2PochiResult {

    private final String conversationId;
    private final String originatorConversationId;
    private final String responseCode;
    private final String responseDescription;
    private final String rawResponse;

    private B2PochiResult(String conversationId, String originatorConversationId, String responseCode,
                           String responseDescription, String rawResponse) {
        this.conversationId = conversationId;
        this.originatorConversationId = originatorConversationId;
        this.responseCode = responseCode;
        this.responseDescription = responseDescription;
        this.rawResponse = rawResponse;
    }

    public static B2PochiResult fromJson(String body) {
        Map<String, Object> root = JsonReader.parseObject(body);
        return new B2PochiResult(
                JsonReader.getString(root, "ConversationID"),
                JsonReader.getString(root, "OriginatorConversationID"),
                JsonReader.getString(root, "ResponseCode"),
                JsonReader.getString(root, "ResponseDescription"),
                body);
    }

    public String getConversationId() {
        return conversationId;
    }

    public String getOriginatorConversationId() {
        return originatorConversationId;
    }

    public String getResponseCode() {
        return responseCode;
    }

    public String getResponseDescription() {
        return responseDescription;
    }

    public boolean isAccepted() {
        return "0".equals(responseCode);
    }

    /** The raw JSON Daraja returned, for anything not exposed by a typed getter. */
    public String getRawResponse() {
        return rawResponse;
    }
}
