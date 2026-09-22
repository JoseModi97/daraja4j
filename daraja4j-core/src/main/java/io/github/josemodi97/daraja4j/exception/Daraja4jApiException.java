package io.github.josemodi97.daraja4j.exception;

/**
 * Thrown when Daraja itself rejects a request &mdash; either a hard HTTP-level
 * failure (invalid OAuth credentials, malformed payload, {@code 4xx}/{@code 5xx})
 * carrying the {@code requestId}/{@code errorCode}/{@code errorMessage} shape
 * Daraja returns for those, or a soft failure where Daraja responds
 * {@code 200 OK} with a body whose {@code ResponseCode}/{@code ResultCode} is
 * not {@code "0"} (Daraja frequently acknowledges a request over HTTP while
 * still rejecting it at the application level).
 */
public class Daraja4jApiException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    private final int httpStatus;
    private final String errorCode;
    private final String errorMessage;
    private final String requestId;

    public Daraja4jApiException(String message, int httpStatus, String errorCode, String errorMessage, String requestId) {
        super(message);
        this.httpStatus = httpStatus;
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
        this.requestId = requestId;
    }

    /** The HTTP status code Daraja responded with. */
    public int getHttpStatus() {
        return httpStatus;
    }

    /** Daraja's {@code errorCode} / {@code ResponseCode} / {@code ResultCode}, if present. */
    public String getErrorCode() {
        return errorCode;
    }

    /** Daraja's {@code errorMessage} / {@code ResponseDescription} / {@code ResultDesc}, if present. */
    public String getErrorMessage() {
        return errorMessage;
    }

    /** Daraja's {@code requestId}, if present (useful when contacting Safaricom support). */
    public String getRequestId() {
        return requestId;
    }
}
