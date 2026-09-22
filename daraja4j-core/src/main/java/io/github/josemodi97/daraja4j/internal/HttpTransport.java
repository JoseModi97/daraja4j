package io.github.josemodi97.daraja4j.internal;

import io.github.josemodi97.daraja4j.exception.Daraja4jTransportException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * Minimal, zero-dependency HTTP client built on {@link HttpURLConnection}
 * &mdash; part of the JDK since Java 1.1 &mdash; so the core module never
 * pulls in a third-party HTTP library. Daraja speaks JSON exclusively, so
 * unlike a form-encoded transport, every request here carries a JSON body
 * (or none) and a {@code Bearer}/{@code Basic} authorization header. Not
 * part of the public API.
 */
public final class HttpTransport {

    private static final int DEFAULT_TIMEOUT_MS = 30_000;

    private HttpTransport() {
    }

    /** POSTs a JSON body with {@code Authorization: Bearer <bearerToken>}. */
    public static HttpResponse postJson(String url, String jsonBody, String bearerToken, int connectTimeoutMs, int readTimeoutMs) {
        return sendJson("POST", url, jsonBody, bearerToken, connectTimeoutMs, readTimeoutMs);
    }

    /**
     * Sends a JSON body with an arbitrary HTTP method and
     * {@code Authorization: Bearer <bearerToken>}. Exists because Daraja's
     * Pull API Query endpoint is documented as {@code GET} with a JSON
     * request body &mdash; non-standard, but {@link HttpURLConnection}
     * permits it (the restriction against a body on GET is an HTTP
     * convention, not something the client enforces).
     */
    public static HttpResponse sendJson(String method, String url, String jsonBody, String bearerToken, int connectTimeoutMs, int readTimeoutMs) {
        return request(url, method, jsonBody, "Bearer " + bearerToken, connectTimeoutMs, readTimeoutMs);
    }

    /**
     * GETs with {@code Authorization: Basic <base64(username:password)>}
     * &mdash; used only for {@code /oauth/v1/generate}, the one Daraja
     * endpoint that isn't a bearer-token JSON call.
     */
    public static HttpResponse getWithBasicAuth(String url, String username, String password, int connectTimeoutMs, int readTimeoutMs) {
        String credentials = (username == null ? "" : username) + ":" + (password == null ? "" : password);
        String basic = Base64.getEncoder().encodeToString(credentials.getBytes(StandardCharsets.UTF_8));
        return request(url, "GET", null, "Basic " + basic, connectTimeoutMs, readTimeoutMs);
    }

    private static HttpResponse request(String url, String method, String jsonBody, String authorizationHeader,
                                         int connectTimeoutMs, int readTimeoutMs) {
        HttpURLConnection connection = null;
        try {
            connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setRequestMethod(method);
            connection.setConnectTimeout(connectTimeoutMs > 0 ? connectTimeoutMs : DEFAULT_TIMEOUT_MS);
            connection.setReadTimeout(readTimeoutMs > 0 ? readTimeoutMs : DEFAULT_TIMEOUT_MS);
            connection.setInstanceFollowRedirects(true);
            connection.setRequestProperty("Accept", "application/json");
            connection.setRequestProperty("Authorization", authorizationHeader);

            if (jsonBody != null) {
                byte[] payload = jsonBody.getBytes(StandardCharsets.UTF_8);
                connection.setDoOutput(true);
                connection.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
                connection.setRequestProperty("Content-Length", String.valueOf(payload.length));
                try (OutputStream out = connection.getOutputStream()) {
                    out.write(payload);
                }
            }

            int status = connection.getResponseCode();
            InputStream stream = status >= 400 ? connection.getErrorStream() : connection.getInputStream();
            String responseBody = stream == null ? "" : readAll(stream);

            return new HttpResponse(url, status, responseBody);
        } catch (IOException e) {
            throw new Daraja4jTransportException("Request to " + url + " failed: " + e.getMessage(), e);
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    private static String readAll(InputStream stream) throws IOException {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8))) {
            char[] buf = new char[4096];
            int read;
            while ((read = reader.read(buf)) != -1) {
                sb.append(buf, 0, read);
            }
        }
        return sb.toString();
    }

    /** Plain HTTP response holder. Not part of the public API. */
    public static final class HttpResponse {
        public final String requestUrl;
        public final int httpStatus;
        public final String body;

        HttpResponse(String requestUrl, int httpStatus, String body) {
            this.requestUrl = requestUrl;
            this.httpStatus = httpStatus;
            this.body = body;
        }
    }
}
