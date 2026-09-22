package io.github.josemodi97.daraja4j.model;

import io.github.josemodi97.daraja4j.internal.JsonReader;
import java.util.Map;

/** The outcome of a {@link StkPushQueryRequest} poll. */
public final class StkPushQueryResult {

    private final String responseCode;
    private final String responseDescription;
    private final String merchantRequestId;
    private final String checkoutRequestId;
    private final String resultCode;
    private final String resultDesc;
    private final String rawResponse;

    private StkPushQueryResult(String responseCode, String responseDescription, String merchantRequestId,
                                String checkoutRequestId, String resultCode, String resultDesc, String rawResponse) {
        this.responseCode = responseCode;
        this.responseDescription = responseDescription;
        this.merchantRequestId = merchantRequestId;
        this.checkoutRequestId = checkoutRequestId;
        this.resultCode = resultCode;
        this.resultDesc = resultDesc;
        this.rawResponse = rawResponse;
    }

    public static StkPushQueryResult fromJson(String body) {
        Map<String, Object> root = JsonReader.parseObject(body);
        return new StkPushQueryResult(
                JsonReader.getString(root, "ResponseCode"),
                JsonReader.getString(root, "ResponseDescription"),
                JsonReader.getString(root, "MerchantRequestID"),
                JsonReader.getString(root, "CheckoutRequestID"),
                JsonReader.getString(root, "ResultCode"),
                JsonReader.getString(root, "ResultDesc"),
                body);
    }

    public String getResponseCode() {
        return responseCode;
    }

    public String getResponseDescription() {
        return responseDescription;
    }

    public String getMerchantRequestId() {
        return merchantRequestId;
    }

    public String getCheckoutRequestId() {
        return checkoutRequestId;
    }

    /** {@code 0} once the payment has completed successfully; a non-zero code on failure; often absent while still pending. */
    public String getResultCode() {
        return resultCode;
    }

    public String getResultDesc() {
        return resultDesc;
    }

    public boolean isSuccess() {
        return "0".equals(resultCode);
    }

    /** The raw JSON Daraja returned, for anything not exposed by a typed getter. */
    public String getRawResponse() {
        return rawResponse;
    }
}
