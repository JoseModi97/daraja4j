package io.github.josemodi97.daraja4j.model;

import io.github.josemodi97.daraja4j.internal.JsonReader;
import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Parses the JSON body Daraja POSTs to an STK Push request's
 * {@code callbackUrl} once the payer has responded to the USSD prompt (or it
 * timed out/was cancelled).
 *
 * <p><strong>This is a structural parser, not a signature verifier</strong> -
 * Daraja sends no cryptographic signature on callbacks. See the project
 * README's Security section for how to establish trust in a callback
 * (unguessable URLs, reconciling via {@link StkPushQueryRequest} before
 * crediting an account, etc.) before acting on the result of {@link #parse}.
 */
public final class StkCallbackResult {

    private final String merchantRequestId;
    private final String checkoutRequestId;
    private final int resultCode;
    private final String resultDesc;
    private final Map<String, Object> metadata;
    private final String rawResponse;

    private StkCallbackResult(String merchantRequestId, String checkoutRequestId, int resultCode,
                               String resultDesc, Map<String, Object> metadata, String rawResponse) {
        this.merchantRequestId = merchantRequestId;
        this.checkoutRequestId = checkoutRequestId;
        this.resultCode = resultCode;
        this.resultDesc = resultDesc;
        this.metadata = metadata;
        this.rawResponse = rawResponse;
    }

    /** Parses a raw STK callback POST body (the {@code Body.stkCallback} envelope). */
    public static StkCallbackResult parse(String rawJsonBody) {
        Map<String, Object> root = JsonReader.parseObject(rawJsonBody);
        Map<String, Object> body = JsonReader.getObject(root, "Body");
        Map<String, Object> callback = body != null ? JsonReader.getObject(body, "stkCallback") : null;
        if (callback == null) {
            callback = root;
        }

        Integer resultCode = JsonReader.getInt(callback, "ResultCode");
        Map<String, Object> metadataMap = new LinkedHashMap<>();
        Map<String, Object> callbackMetadata = JsonReader.getObject(callback, "CallbackMetadata");
        if (callbackMetadata != null) {
            List<Object> items = JsonReader.getArray(callbackMetadata, "Item");
            if (items != null) {
                for (Object item : items) {
                    if (item instanceof Map) {
                        @SuppressWarnings("unchecked")
                        Map<String, Object> itemMap = (Map<String, Object>) item;
                        Object name = itemMap.get("Name");
                        if (name != null) {
                            metadataMap.put(String.valueOf(name), itemMap.get("Value"));
                        }
                    }
                }
            }
        }

        return new StkCallbackResult(
                JsonReader.getString(callback, "MerchantRequestID"),
                JsonReader.getString(callback, "CheckoutRequestID"),
                resultCode != null ? resultCode : -1,
                JsonReader.getString(callback, "ResultDesc"),
                metadataMap,
                rawJsonBody);
    }

    public String getMerchantRequestId() {
        return merchantRequestId;
    }

    public String getCheckoutRequestId() {
        return checkoutRequestId;
    }

    /** {@code 0} means the payment succeeded; any other value is a failure/cancellation code. */
    public int getResultCode() {
        return resultCode;
    }

    public String getResultDesc() {
        return resultDesc;
    }

    public boolean isSuccess() {
        return resultCode == 0;
    }

    /** {@code CallbackMetadata.Item[].Value} where {@code Name == "Amount"}, present only on success. */
    public BigDecimal getAmount() {
        return toBigDecimal(metadata.get("Amount"));
    }

    /** {@code CallbackMetadata.Item[].Value} where {@code Name == "MpesaReceiptNumber"}, present only on success. */
    public String getMpesaReceiptNumber() {
        Object value = metadata.get("MpesaReceiptNumber");
        return value == null ? null : String.valueOf(value);
    }

    /** {@code CallbackMetadata.Item[].Value} where {@code Name == "TransactionDate"} ({@code yyyyMMddHHmmss}), present only on success. */
    public String getTransactionDate() {
        Object value = metadata.get("TransactionDate");
        return value == null ? null : String.valueOf(value);
    }

    /** {@code CallbackMetadata.Item[].Value} where {@code Name == "PhoneNumber"}, present only on success. */
    public String getPhoneNumber() {
        Object value = metadata.get("PhoneNumber");
        return value == null ? null : String.valueOf(value);
    }

    /** Any other {@code CallbackMetadata.Item[]} entry by its {@code Name}, for fields not exposed by a typed getter. */
    public Object getMetadataValue(String name) {
        return metadata.get(name);
    }

    /** The raw JSON Daraja posted, for anything not exposed by a typed getter. */
    public String getRawResponse() {
        return rawResponse;
    }

    private static BigDecimal toBigDecimal(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof BigDecimal) {
            return (BigDecimal) value;
        }
        return new BigDecimal(String.valueOf(value));
    }
}
