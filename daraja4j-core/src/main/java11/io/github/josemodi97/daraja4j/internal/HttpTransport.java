package io.github.josemodi97.daraja4j.internal;

import io.github.josemodi97.daraja4j.exception.Daraja4jTransportException;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse.BodyHandlers;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Base64;

/**
 * Java 11+ variant of {@code HttpTransport}, built on {@link HttpClient}
 * instead of {@link java.net.HttpURLConnection}: negotiates HTTP/2
 * automatically (falling back to HTTP/1.1), and is virtual-thread-friendly
 * on Java 21+.
 *
 * <p>Packaged into {@code META-INF/versions/11/} of the multi-release core
 * jar. A Java 11+ JVM loads this class transparently in place of the
 * Java-8 base version under {@code src/main/java}; the public API
 * (including the nested {@code HttpResponse} shape) is kept identical on
 * purpose, since callers must not need to know which variant is active.
 */
public final class HttpTransport {

    private static final int DEFAULT_TIMEOUT_MS = 30_000;

    private static final HttpClient CLIENT = HttpClient.newBuilder()
            .connectTimeout(Duration.ofMillis(DEFAULT_TIMEOUT_MS))
            .followRedirects(HttpClient.Redirect.NORMAL)
            .build();

    private HttpTransport() {
    }

    public static HttpResponse postJson(String url, String jsonBody, String bearerToken, int connectTimeoutMs, int readTimeoutMs) {
        return sendJson("POST", url, jsonBody, bearerToken, connectTimeoutMs, readTimeoutMs);
    }

    public static HttpResponse sendJson(String method, String url, String jsonBody, String bearerToken, int connectTimeoutMs, int readTimeoutMs) {
        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .timeout(timeout(readTimeoutMs))
                .header("Accept", "application/json")
                .header("Authorization", "Bearer " + bearerToken);

        if (jsonBody != null) {
            builder.header("Content-Type", "application/json;charset=UTF-8")
                    .method(method, BodyPublishers.ofString(jsonBody, StandardCharsets.UTF_8));
        } else {
            builder.method(method, BodyPublishers.noBody());
        }

        return send(builder.build(), url);
    }

    public static HttpResponse getWithBasicAuth(String url, String username, String password, int connectTimeoutMs, int readTimeoutMs) {
        String credentials = (username == null ? "" : username) + ":" + (password == null ? "" : password);
        String basic = Base64.getEncoder().encodeToString(credentials.getBytes(StandardCharsets.UTF_8));
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .timeout(timeout(readTimeoutMs))
                .header("Accept", "application/json")
                .header("Authorization", "Basic " + basic)
                .GET()
                .build();
        return send(request, url);
    }

    private static HttpResponse send(HttpRequest request, String url) {
        try {
            java.net.http.HttpResponse<String> response = CLIENT.send(request, BodyHandlers.ofString(StandardCharsets.UTF_8));
            return new HttpResponse(url, response.statusCode(), response.body());
        } catch (IOException e) {
            throw new Daraja4jTransportException("Request to " + url + " failed: " + e.getMessage(), e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new Daraja4jTransportException("Request to " + url + " was interrupted", new IOException(e));
        }
    }

    private static Duration timeout(int timeoutMs) {
        return Duration.ofMillis(timeoutMs > 0 ? timeoutMs : DEFAULT_TIMEOUT_MS);
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
