package io.github.josemodi97.daraja4j.internal;

import io.github.josemodi97.daraja4j.Daraja4jConfig;
import io.github.josemodi97.daraja4j.exception.Daraja4jApiException;
import java.util.Map;

/**
 * Caches the OAuth2 client-credentials access token Daraja issues, so
 * {@link io.github.josemodi97.daraja4j.Daraja4jClient} callers never fetch or
 * pass a token themselves. Thread-safe: concurrent callers racing on an
 * expired/absent token block briefly on one fetch rather than each fetching
 * their own. Not part of the public API.
 */
public final class AccessTokenCache {

    private final Daraja4jConfig config;
    private volatile CachedToken cached;

    public AccessTokenCache(Daraja4jConfig config) {
        this.config = config;
    }

    /**
     * Returns a currently-valid token, fetching (and caching) a new one if
     * there is none yet or the cached one is at/past its expiry minus
     * {@link Daraja4jConfig#getTokenExpirySafetyMarginSeconds()}.
     */
    public String getToken() {
        CachedToken token = cached;
        if (token != null && !token.isExpiredWithin(config.getTokenExpirySafetyMarginSeconds())) {
            return token.value;
        }
        synchronized (this) {
            token = cached;
            if (token != null && !token.isExpiredWithin(config.getTokenExpirySafetyMarginSeconds())) {
                return token.value;
            }
            token = fetchNewToken();
            cached = token;
            return token.value;
        }
    }

    /** Discards the cached token (if any) and fetches a fresh one unconditionally. */
    public String forceRefresh() {
        synchronized (this) {
            CachedToken token = fetchNewToken();
            cached = token;
            return token.value;
        }
    }

    private CachedToken fetchNewToken() {
        String url = config.getResolvedBaseUrl() + "/oauth/v1/generate?grant_type=client_credentials";
        HttpTransport.HttpResponse response = HttpTransport.getWithBasicAuth(
                url, config.getConsumerKey(), config.getConsumerSecret(),
                config.getConnectTimeoutMillis(), config.getReadTimeoutMillis());

        Map<String, Object> body;
        try {
            body = JsonReader.parseObject(response.body);
        } catch (RuntimeException e) {
            throw ApiErrors.fromErrorResponse(response);
        }

        if (response.httpStatus >= 400) {
            throw ApiErrors.fromParsedBody(body, response.requestUrl, response.httpStatus);
        }

        String accessToken = JsonReader.getString(body, "access_token");
        if (accessToken == null || accessToken.trim().isEmpty()) {
            throw new Daraja4jApiException(
                    "Daraja did not return an access_token from " + url + ": " + response.body,
                    response.httpStatus, null, null, null);
        }

        Integer expiresInSeconds = JsonReader.getInt(body, "expires_in");
        long ttlSeconds = expiresInSeconds != null ? expiresInSeconds : 3600L;
        long expiresAtEpochMillis = System.currentTimeMillis() + ttlSeconds * 1000L;

        return new CachedToken(accessToken, expiresAtEpochMillis);
    }

    private static final class CachedToken {
        final String value;
        final long expiresAtEpochMillis;

        CachedToken(String value, long expiresAtEpochMillis) {
            this.value = value;
            this.expiresAtEpochMillis = expiresAtEpochMillis;
        }

        boolean isExpiredWithin(int safetyMarginSeconds) {
            return System.currentTimeMillis() >= (expiresAtEpochMillis - safetyMarginSeconds * 1000L);
        }
    }
}
