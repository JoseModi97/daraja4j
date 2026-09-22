package io.github.josemodi97.daraja4j.internal;

import io.github.josemodi97.daraja4j.exception.Daraja4jApiException;
import java.util.Map;

/**
 * Turns a failing {@link HttpTransport.HttpResponse} into a
 * {@link Daraja4jApiException}, shared by every operation and by the OAuth
 * token fetch. Daraja uses a few different field names for the same concept
 * depending on the endpoint ({@code errorCode}/{@code errorMessage} on hard
 * OAuth/validation failures, {@code ResponseCode}/{@code ResponseDescription}
 * on synchronous request acks, {@code ResultCode}/{@code ResultDesc} on
 * callback bodies) &mdash; this checks all of them. Not part of the public API.
 */
public final class ApiErrors {

    private ApiErrors() {
    }

    public static Daraja4jApiException fromErrorResponse(HttpTransport.HttpResponse response) {
        Map<String, Object> body;
        try {
            body = JsonReader.parseObject(response.body);
        } catch (RuntimeException e) {
            return new Daraja4jApiException(
                    "Daraja request to " + response.requestUrl + " failed with HTTP " + response.httpStatus
                            + ": " + response.body,
                    response.httpStatus, null, null, null);
        }
        return fromParsedBody(body, response.requestUrl, response.httpStatus);
    }

    public static Daraja4jApiException fromParsedBody(Map<String, Object> body, String requestUrl, int httpStatus) {
        String errorCode = firstNonNull(
                JsonReader.getString(body, "errorCode"),
                JsonReader.getString(body, "ResponseCode"),
                JsonReader.getString(body, "ResultCode"));
        String errorMessage = firstNonNull(
                JsonReader.getString(body, "errorMessage"),
                JsonReader.getString(body, "ResponseDescription"),
                JsonReader.getString(body, "ResultDesc"));
        String requestId = firstNonNull(
                JsonReader.getString(body, "requestId"),
                JsonReader.getString(body, "OriginatorConversationID"),
                JsonReader.getString(body, "ConversationID"));
        String message = "Daraja request to " + requestUrl + " failed"
                + (httpStatus > 0 ? " with HTTP " + httpStatus : "")
                + (errorMessage != null ? ": " + errorMessage : "");
        return new Daraja4jApiException(message, httpStatus, errorCode, errorMessage, requestId);
    }

    private static String firstNonNull(String... values) {
        for (String value : values) {
            if (value != null) {
                return value;
            }
        }
        return null;
    }
}
