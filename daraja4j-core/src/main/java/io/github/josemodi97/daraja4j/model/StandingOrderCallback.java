package io.github.josemodi97.daraja4j.model;

import io.github.josemodi97.daraja4j.internal.JsonReader;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Parses the JSON body Daraja posts to a {@link StandingOrderRequest}'s
 * {@code callbackUrl} once the standing order is created (or fails). Uses
 * lowercase {@code responseHeader}/{@code responseBody} keys and a
 * {@code responseData} name/value array - a different shape from every other
 * Daraja callback in this SDK.
 *
 * <p><strong>This is a structural parser, not a signature verifier</strong> -
 * see the project README's Security section for how to establish trust in a
 * callback before acting on it.
 */
public final class StandingOrderCallback {

    private final String responseRefId;
    private final String requestRefId;
    private final String responseCode;
    private final String responseDescription;
    private final Map<String, Object> responseData;
    private final String rawResponse;

    private StandingOrderCallback(String responseRefId, String requestRefId, String responseCode,
                                   String responseDescription, Map<String, Object> responseData, String rawResponse) {
        this.responseRefId = responseRefId;
        this.requestRefId = requestRefId;
        this.responseCode = responseCode;
        this.responseDescription = responseDescription;
        this.responseData = responseData;
        this.rawResponse = rawResponse;
    }

    public static StandingOrderCallback parse(String rawJsonBody) {
        Map<String, Object> root = JsonReader.parseObject(rawJsonBody);
        Map<String, Object> header = JsonReader.getObject(root, "responseHeader");
        Map<String, Object> body = JsonReader.getObject(root, "responseBody");

        Map<String, Object> data = new LinkedHashMap<>();
        if (body != null) {
            List<Object> items = JsonReader.getArray(body, "responseData");
            if (items != null) {
                for (Object item : items) {
                    if (item instanceof Map) {
                        @SuppressWarnings("unchecked")
                        Map<String, Object> itemMap = (Map<String, Object>) item;
                        Object name = itemMap.get("name");
                        if (name != null) {
                            data.put(String.valueOf(name), itemMap.get("value"));
                        }
                    }
                }
            }
        }

        return new StandingOrderCallback(
                header != null ? JsonReader.getString(header, "responseRefID") : null,
                header != null ? JsonReader.getString(header, "requestRefID") : null,
                header != null ? JsonReader.getString(header, "responseCode") : null,
                header != null ? JsonReader.getString(header, "responseDescription") : null,
                data,
                rawJsonBody);
    }

    public String getResponseRefId() {
        return responseRefId;
    }

    public String getRequestRefId() {
        return requestRefId;
    }

    /** {@code "0"} on success. */
    public String getResponseCode() {
        return responseCode;
    }

    public String getResponseDescription() {
        return responseDescription;
    }

    public boolean isSuccess() {
        return "0".equals(responseCode);
    }

    /** A {@code responseBody.responseData[]} entry by its {@code name} (e.g. {@code "status"}, {@code "TransactionID"}). */
    public Object getDataValue(String name) {
        return responseData.get(name);
    }

    /** All {@code responseBody.responseData[]} entries, keyed by {@code name}. */
    public Map<String, Object> getResponseData() {
        return responseData;
    }

    /** The raw JSON Daraja posted, for anything not exposed by a typed getter. */
    public String getRawResponse() {
        return rawResponse;
    }
}
