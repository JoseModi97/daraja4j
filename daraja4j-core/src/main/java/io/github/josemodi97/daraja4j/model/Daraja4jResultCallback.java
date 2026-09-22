package io.github.josemodi97.daraja4j.model;

import io.github.josemodi97.daraja4j.internal.JsonReader;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Parses the {@code Result} envelope Daraja posts to {@code resultUrl} for
 * B2C, B2B, B2Pochi, Reversal, Account Balance, and Transaction Status
 * requests - they all share this one shape, differing only in which
 * {@code ResultParameters.ResultParameter[]} keys are populated.
 *
 * <p><strong>This is a structural parser, not a signature verifier</strong> -
 * see the project README's Security section for how to establish trust in a
 * callback before acting on it. Note {@link #getResultCode()} is a
 * {@code String}: Daraja returns a numeric code on success ({@code "0"}) but
 * an alphanumeric one on some failures (e.g. {@code "R000002"}).
 */
public final class Daraja4jResultCallback {

    private final int resultType;
    private final String resultCode;
    private final String resultDesc;
    private final String originatorConversationId;
    private final String conversationId;
    private final String transactionId;
    private final Map<String, Object> resultParameters;
    private final String rawResponse;

    private Daraja4jResultCallback(int resultType, String resultCode, String resultDesc, String originatorConversationId,
                                    String conversationId, String transactionId, Map<String, Object> resultParameters,
                                    String rawResponse) {
        this.resultType = resultType;
        this.resultCode = resultCode;
        this.resultDesc = resultDesc;
        this.originatorConversationId = originatorConversationId;
        this.conversationId = conversationId;
        this.transactionId = transactionId;
        this.resultParameters = resultParameters;
        this.rawResponse = rawResponse;
    }

    /** Parses a raw {@code resultUrl} POST body (the {@code Result} envelope). */
    public static Daraja4jResultCallback parse(String rawJsonBody) {
        Map<String, Object> root = JsonReader.parseObject(rawJsonBody);
        Map<String, Object> result = JsonReader.getObject(root, "Result");
        if (result == null) {
            result = root;
        }

        Integer resultType = JsonReader.getInt(result, "ResultType");
        Map<String, Object> parameters = new LinkedHashMap<>();
        Map<String, Object> resultParameters = JsonReader.getObject(result, "ResultParameters");
        if (resultParameters != null) {
            List<Object> items = JsonReader.getArray(resultParameters, "ResultParameter");
            if (items != null) {
                for (Object item : items) {
                    if (item instanceof Map) {
                        @SuppressWarnings("unchecked")
                        Map<String, Object> itemMap = (Map<String, Object>) item;
                        Object key = itemMap.get("Key");
                        if (key != null) {
                            parameters.put(String.valueOf(key), itemMap.get("Value"));
                        }
                    }
                }
            }
        }

        return new Daraja4jResultCallback(
                resultType != null ? resultType : -1,
                JsonReader.getString(result, "ResultCode"),
                JsonReader.getString(result, "ResultDesc"),
                JsonReader.getString(result, "OriginatorConversationID"),
                JsonReader.getString(result, "ConversationID"),
                JsonReader.getString(result, "TransactionID"),
                parameters,
                rawJsonBody);
    }

    /** {@code 0} = completed, {@code 1} = waiting for further messages. */
    public int getResultType() {
        return resultType;
    }

    /** {@code "0"} on success; an alphanumeric failure code otherwise (e.g. {@code "R000002"}). */
    public String getResultCode() {
        return resultCode;
    }

    public String getResultDesc() {
        return resultDesc;
    }

    public boolean isSuccess() {
        return "0".equals(resultCode);
    }

    public String getOriginatorConversationId() {
        return originatorConversationId;
    }

    public String getConversationId() {
        return conversationId;
    }

    /** The M-Pesa receipt number for this transaction, when applicable. */
    public String getTransactionId() {
        return transactionId;
    }

    /** A {@code ResultParameters.ResultParameter[]} entry by its {@code Key}, for fields not exposed by a typed getter. */
    public Object getResultParameter(String key) {
        return resultParameters.get(key);
    }

    /** All {@code ResultParameters.ResultParameter[]} entries, keyed by {@code Key}. */
    public Map<String, Object> getResultParameters() {
        return resultParameters;
    }

    /** The raw JSON Daraja posted, for anything not exposed by a typed getter. */
    public String getRawResponse() {
        return rawResponse;
    }
}
