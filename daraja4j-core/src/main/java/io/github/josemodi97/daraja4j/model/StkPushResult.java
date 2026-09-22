package io.github.josemodi97.daraja4j.model;

import io.github.josemodi97.daraja4j.internal.JsonReader;
import java.util.Map;

/**
 * Daraja's synchronous acknowledgement of an {@link StkPushRequest}. This is
 * <strong>not</strong> the payment outcome &mdash; only that the USSD prompt
 * was sent. The actual result arrives later at the request's
 * {@code callbackUrl} (parse it with {@link StkCallbackResult#parse(String)}),
 * or can be polled with {@link StkPushQueryRequest}.
 */
public final class StkPushResult {

    private final String merchantRequestId;
    private final String checkoutRequestId;
    private final String responseCode;
    private final String responseDescription;
    private final String customerMessage;
    private final String rawResponse;

    private StkPushResult(String merchantRequestId, String checkoutRequestId, String responseCode,
                           String responseDescription, String customerMessage, String rawResponse) {
        this.merchantRequestId = merchantRequestId;
        this.checkoutRequestId = checkoutRequestId;
        this.responseCode = responseCode;
        this.responseDescription = responseDescription;
        this.customerMessage = customerMessage;
        this.rawResponse = rawResponse;
    }

    public static StkPushResult fromJson(String body) {
        Map<String, Object> root = JsonReader.parseObject(body);
        return new StkPushResult(
                JsonReader.getString(root, "MerchantRequestID"),
                JsonReader.getString(root, "CheckoutRequestID"),
                JsonReader.getString(root, "ResponseCode"),
                JsonReader.getString(root, "ResponseDescription"),
                JsonReader.getString(root, "CustomerMessage"),
                body);
    }

    public String getMerchantRequestId() {
        return merchantRequestId;
    }

    public String getCheckoutRequestId() {
        return checkoutRequestId;
    }

    public String getResponseCode() {
        return responseCode;
    }

    public String getResponseDescription() {
        return responseDescription;
    }

    public String getCustomerMessage() {
        return customerMessage;
    }

    public boolean isAccepted() {
        return "0".equals(responseCode);
    }

    /** The raw JSON Daraja returned, for anything not exposed by a typed getter. */
    public String getRawResponse() {
        return rawResponse;
    }
}
