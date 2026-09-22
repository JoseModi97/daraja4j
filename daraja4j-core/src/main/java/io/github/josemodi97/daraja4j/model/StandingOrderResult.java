package io.github.josemodi97.daraja4j.model;

import io.github.josemodi97.daraja4j.internal.JsonReader;
import java.util.Map;

/**
 * Daraja's synchronous acknowledgement of a {@link StandingOrderRequest}.
 * Response fields are nested under {@code ResponseHeader}/{@code ResponseBody}
 * rather than flat, unlike most other Daraja operations.
 */
public final class StandingOrderResult {

    private final String responseRefId;
    private final String responseCode;
    private final String responseDescription;
    private final String resultDesc;
    private final String rawResponse;

    private StandingOrderResult(String responseRefId, String responseCode, String responseDescription,
                                 String resultDesc, String rawResponse) {
        this.responseRefId = responseRefId;
        this.responseCode = responseCode;
        this.responseDescription = responseDescription;
        this.resultDesc = resultDesc;
        this.rawResponse = rawResponse;
    }

    public static StandingOrderResult fromJson(String body) {
        Map<String, Object> root = JsonReader.parseObject(body);
        Map<String, Object> header = JsonReader.getObject(root, "ResponseHeader");
        Map<String, Object> responseBody = JsonReader.getObject(root, "ResponseBody");

        String responseCode = header != null ? JsonReader.getString(header, "responseCode") : null;
        if (responseCode == null && responseBody != null) {
            responseCode = JsonReader.getString(responseBody, "responseCode");
        }
        String responseDescription = header != null ? JsonReader.getString(header, "responseDescription") : null;
        if (responseDescription == null && responseBody != null) {
            responseDescription = JsonReader.getString(responseBody, "responseDescription");
        }

        return new StandingOrderResult(
                header != null ? JsonReader.getString(header, "responseRefID") : null,
                responseCode,
                responseDescription,
                header != null ? JsonReader.getString(header, "ResultDesc") : null,
                body);
    }

    public String getResponseRefId() {
        return responseRefId;
    }

    /** {@code "200"} on success (this endpoint uses HTTP-status-shaped codes, not {@code "0"}). */
    public String getResponseCode() {
        return responseCode;
    }

    public String getResponseDescription() {
        return responseDescription;
    }

    public String getResultDesc() {
        return resultDesc;
    }

    public boolean isAccepted() {
        return "200".equals(responseCode);
    }

    /** The raw JSON Daraja returned, for anything not exposed by a typed getter. */
    public String getRawResponse() {
        return rawResponse;
    }
}
