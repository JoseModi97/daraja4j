package io.github.josemodi97.daraja4j;

import io.github.josemodi97.daraja4j.internal.AccessTokenCache;
import io.github.josemodi97.daraja4j.internal.ApiErrors;
import io.github.josemodi97.daraja4j.internal.HttpTransport;
import io.github.josemodi97.daraja4j.internal.JsonReader;
import java.util.Map;
import java.util.function.Function;

/**
 * Low-level execution engine: owns the OAuth token cache and sends every
 * signed/serialized request built by a {@code .model} request class, then
 * hands the raw response body to that operation's {@code fromJson} parser.
 *
 * <p>Most applications should use {@link Daraja4jClient} instead; reach for
 * this class directly only if you need to compose a request with a custom
 * transport or parsing step.
 */
public final class Daraja4jGateway {

    private final Daraja4jConfig config;
    private final AccessTokenCache tokenCache;

    public Daraja4jGateway(Daraja4jConfig config) {
        this.config = config;
        this.config.validate();
        this.tokenCache = new AccessTokenCache(config);
    }

    public Daraja4jConfig getConfig() {
        return config;
    }

    /** Forces a fresh OAuth token fetch, discarding any cached one. */
    public String refreshToken() {
        return tokenCache.forceRefresh();
    }

    /**
     * POSTs {@code requestJson} to {@code path} with a valid bearer token,
     * then parses the response with {@code parser} - after checking both the
     * HTTP status and (when {@code resultCodeField} is non-null) that the
     * top-level {@code resultCodeField} equals {@code "0"}, since Daraja
     * frequently acknowledges a request with {@code 200 OK} while still
     * rejecting it at the application level. Pass {@code null} for
     * operations whose success code isn't a top-level {@code "0"} (Pull
     * Transactions and Ratiba use different fields/values/nesting) - their
     * {@code Result} classes expose their own {@code isSuccess()}/
     * {@code isAccepted()} instead.
     */
    public <T> T execute(String path, String requestJson, Function<String, T> parser, String resultCodeField) {
        return execute("POST", path, requestJson, parser, resultCodeField, "0");
    }

    /** {@link #execute(String, String, Function, String)} with an explicit HTTP method and expected success value. */
    public <T> T execute(String method, String path, String requestJson, Function<String, T> parser,
                          String resultCodeField, String expectedSuccessValue) {
        String url = config.getResolvedBaseUrl() + path;
        String token = tokenCache.getToken();
        HttpTransport.HttpResponse response = HttpTransport.sendJson(
                method, url, requestJson, token, config.getConnectTimeoutMillis(), config.getReadTimeoutMillis());

        Map<String, Object> body;
        try {
            body = JsonReader.parseObject(response.body);
        } catch (RuntimeException e) {
            throw ApiErrors.fromErrorResponse(response);
        }

        if (response.httpStatus >= 400) {
            throw ApiErrors.fromParsedBody(body, response.requestUrl, response.httpStatus);
        }

        if (resultCodeField != null) {
            String resultCode = JsonReader.getString(body, resultCodeField);
            if (resultCode != null && !expectedSuccessValue.equals(resultCode)) {
                throw ApiErrors.fromParsedBody(body, response.requestUrl, response.httpStatus);
            }
        }

        return parser.apply(response.body);
    }
}
