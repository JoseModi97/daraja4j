package io.github.josemodi97.daraja4j.model;

import io.github.josemodi97.daraja4j.internal.JsonReader;
import java.util.Base64;
import java.util.Map;

/**
 * The outcome of a {@link QrCodeRequest}: returns response metadata
 * and the Base64-encoded PNG image string of the generated QR code.
 */
public final class QrCodeResult {

    private final String responseCode;
    private final String responseDescription;
    private final String requestId;
    private final String qrCode;

    public QrCodeResult(String responseCode, String responseDescription, String requestId, String qrCode) {
        this.responseCode = responseCode;
        this.responseDescription = responseDescription;
        this.requestId = requestId;
        this.qrCode = qrCode;
    }

    public static QrCodeResult fromJson(String json) {
        Map<String, Object> map = JsonReader.parseObject(json);
        return new QrCodeResult(
                JsonReader.getString(map, "ResponseCode"),
                JsonReader.getString(map, "ResponseDescription"),
                JsonReader.getString(map, "RequestID"),
                JsonReader.getString(map, "QRCode")
        );
    }

    /**
     * Whether Daraja acknowledged generating the QR code successfully
     * ({@code ResponseCode} is {@code "00"} or {@code "0"}).
     */
    public boolean isSuccess() {
        return "00".equals(responseCode) || "0".equals(responseCode);
    }

    /** Alias for {@link #isSuccess()}. */
    public boolean isAccepted() {
        return isSuccess();
    }

    public String getResponseCode() {
        return responseCode;
    }

    public String getResponseDescription() {
        return responseDescription;
    }

    public String getRequestId() {
        return requestId;
    }

    /** Returns the raw Base64-encoded PNG image string of the generated QR code. */
    public String getQrCode() {
        return qrCode;
    }

    /** Decodes the {@code qrCode} Base64 string into raw PNG image bytes, ready to save to disk or stream. */
    public byte[] toPngBytes() {
        if (qrCode == null || qrCode.isEmpty()) {
            return new byte[0];
        }
        return Base64.getDecoder().decode(qrCode);
    }
}
