package io.github.josemodi97.daraja4j.model;

import io.github.josemodi97.daraja4j.internal.JsonReader;
import java.util.Map;

/**
 * The outcome of a {@link RegisterC2bUrlsRequest}.
 *
 * <p>Note: Daraja's own response for this endpoint spells the identifier
 * field {@code OriginatorCoversationID} (missing the 'n' in "Conversation")
 * rather than {@code OriginatorConversationID} as used elsewhere in the API -
 * {@link #getOriginatorConversationId()} checks both spellings so callers
 * don't need to know about the typo.
 */
public final class RegisterC2bUrlsResult {

    private final String originatorConversationId;
    private final String responseCode;
    private final String responseDescription;
    private final String rawResponse;

    private RegisterC2bUrlsResult(String originatorConversationId, String responseCode, String responseDescription, String rawResponse) {
        this.originatorConversationId = originatorConversationId;
        this.responseCode = responseCode;
        this.responseDescription = responseDescription;
        this.rawResponse = rawResponse;
    }

    public static RegisterC2bUrlsResult fromJson(String body) {
        Map<String, Object> root = JsonReader.parseObject(body);
        String originatorConversationId = JsonReader.getString(root, "OriginatorCoversationID");
        if (originatorConversationId == null) {
            originatorConversationId = JsonReader.getString(root, "OriginatorConversationID");
        }
        return new RegisterC2bUrlsResult(
                originatorConversationId,
                JsonReader.getString(root, "ResponseCode"),
                JsonReader.getString(root, "ResponseDescription"),
                body);
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
