package io.github.josemodi97.daraja4j.model;

import io.github.josemodi97.daraja4j.internal.JsonReader;
import java.util.Map;

/** The outcome of a {@link PullTransactionsRegisterRequest}. */
public final class PullTransactionsRegisterResult {

    private final String responseRefId;
    private final String responseStatus;
    private final String shortcode;
    private final String responseDescription;
    private final String rawResponse;

    private PullTransactionsRegisterResult(String responseRefId, String responseStatus, String shortcode,
                                            String responseDescription, String rawResponse) {
        this.responseRefId = responseRefId;
        this.responseStatus = responseStatus;
        this.shortcode = shortcode;
        this.responseDescription = responseDescription;
        this.rawResponse = rawResponse;
    }

    public static PullTransactionsRegisterResult fromJson(String body) {
        Map<String, Object> root = JsonReader.parseObject(body);
        return new PullTransactionsRegisterResult(
                JsonReader.getString(root, "ResponseRefID"),
                JsonReader.getString(root, "ResponseStatus"),
                JsonReader.getString(root, "ShortCode"),
                JsonReader.getString(root, "ResponseDescription"),
                body);
    }

    public String getResponseRefId() {
        return responseRefId;
    }

    /** {@code "1000"} = registered successfully, {@code "1001"} = already registered. */
    public String getResponseStatus() {
        return responseStatus;
    }

    public String getShortcode() {
        return shortcode;
    }

    public String getResponseDescription() {
        return responseDescription;
    }

    public boolean isSuccess() {
        return "1000".equals(responseStatus);
    }

    /** The raw JSON Daraja returned, for anything not exposed by a typed getter. */
    public String getRawResponse() {
        return rawResponse;
    }
}
