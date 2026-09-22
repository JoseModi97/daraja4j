package io.github.josemodi97.daraja4j.model;

import io.github.josemodi97.daraja4j.internal.JsonReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * The transactions returned by a {@link PullTransactionsQueryRequest}. Each
 * entry in {@link #getTransactions()} is the raw JSON object Daraja returned
 * for that transaction (typically containing {@code transactionId},
 * {@code trxDate}, {@code msisdn}, {@code transactiontype},
 * {@code billreference}, {@code amount}, {@code organizationname}) - exposed
 * as a raw map rather than a typed class since Daraja's own documentation of
 * this shape has been inconsistent across API versions.
 */
public final class PullTransactionsQueryResult {

    private final String responseRefId;
    private final String responseCode;
    private final String responseMessage;
    private final List<Map<String, Object>> transactions;
    private final String rawResponse;

    private PullTransactionsQueryResult(String responseRefId, String responseCode, String responseMessage,
                                         List<Map<String, Object>> transactions, String rawResponse) {
        this.responseRefId = responseRefId;
        this.responseCode = responseCode;
        this.responseMessage = responseMessage;
        this.transactions = transactions;
        this.rawResponse = rawResponse;
    }

    @SuppressWarnings("unchecked")
    public static PullTransactionsQueryResult fromJson(String body) {
        Map<String, Object> root = JsonReader.parseObject(body);
        List<Object> responseArray = JsonReader.getArray(root, "Response");
        List<Map<String, Object>> transactions = new ArrayList<>();
        flatten(responseArray, transactions);

        return new PullTransactionsQueryResult(
                JsonReader.getString(root, "ResponseRefID"),
                JsonReader.getString(root, "ResponseCode"),
                JsonReader.getString(root, "ResponseMessage"),
                transactions,
                body);
    }

    @SuppressWarnings("unchecked")
    private static void flatten(List<Object> array, List<Map<String, Object>> out) {
        if (array == null) {
            return;
        }
        for (Object element : array) {
            if (element instanceof Map) {
                out.add((Map<String, Object>) element);
            } else if (element instanceof List) {
                flatten((List<Object>) element, out);
            }
        }
    }

    public String getResponseRefId() {
        return responseRefId;
    }

    /** {@code "1000"} on success. */
    public String getResponseCode() {
        return responseCode;
    }

    public String getResponseMessage() {
        return responseMessage;
    }

    public boolean isSuccess() {
        return "1000".equals(responseCode);
    }

    /** The raw transaction objects Daraja returned, flattened from whatever nesting depth it used. */
    public List<Map<String, Object>> getTransactions() {
        return transactions;
    }

    /** The raw JSON Daraja returned, for anything not exposed by a typed getter. */
    public String getRawResponse() {
        return rawResponse;
    }
}
