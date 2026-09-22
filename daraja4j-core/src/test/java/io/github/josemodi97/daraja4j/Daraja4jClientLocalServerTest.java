package io.github.josemodi97.daraja4j;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import io.github.josemodi97.daraja4j.exception.Daraja4jApiException;
import io.github.josemodi97.daraja4j.model.StkPushRequest;
import io.github.josemodi97.daraja4j.model.StkPushResult;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * End-to-end tests against a real local {@link HttpServer} standing in for
 * Daraja, exercising the whole stack: OAuth token fetch/caching, JSON
 * request serialization, and response parsing - without mocking
 * {@code HttpTransport} directly (it's {@code final} with static methods).
 */
class Daraja4jClientLocalServerTest {

    private static final String STK_SUCCESS_BODY = "{"
            + "\"MerchantRequestID\":\"29115-34620561-1\","
            + "\"CheckoutRequestID\":\"ws_CO_191220191020363925\","
            + "\"ResponseCode\":\"0\","
            + "\"ResponseDescription\":\"Success. Request accepted for processing\","
            + "\"CustomerMessage\":\"Success. Request accepted for processing\"}";

    private HttpServer server;
    private final AtomicInteger tokenRequests = new AtomicInteger();
    private volatile String stkResponseBody = STK_SUCCESS_BODY;
    private volatile int stkResponseStatus = 200;

    @BeforeEach
    void startServer() throws IOException {
        server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        server.createContext("/oauth/v1/generate", exchange -> {
            tokenRequests.incrementAndGet();
            respond(exchange, 200, "{\"access_token\":\"fake-token\",\"expires_in\":\"3599\"}");
        });
        server.createContext("/mpesa/stkpush/v1/processrequest", exchange -> respond(exchange, stkResponseStatus, stkResponseBody));
        server.start();
    }

    @AfterEach
    void stopServer() {
        server.stop(0);
    }

    private String baseUrl() {
        return "http://127.0.0.1:" + server.getAddress().getPort();
    }

    private static void respond(HttpExchange exchange, int status, String body) throws IOException {
        byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().add("Content-Type", "application/json");
        exchange.sendResponseHeaders(status, bytes.length);
        try (OutputStream out = exchange.getResponseBody()) {
            out.write(bytes);
        }
        exchange.close();
    }

    private Daraja4jClient newClient() {
        return new Daraja4jClient(Daraja4jConfig.builder()
                .consumerKey("test-key")
                .consumerSecret("test-secret")
                .baseUrl(baseUrl())
                .defaultShortcode("174379")
                .defaultPasskey("bfb279f9aa9bdbcf158e97dd71a467cd2e0c893059b10f78e6b72ada1ed2c919")
                .defaultCallbackUrl("https://example.com/callback")
                .build());
    }

    private StkPushRequest sampleRequest() {
        return StkPushRequest.builder()
                .amount(1)
                .partyA("254708374149")
                .accountReference("INV-0001")
                .build();
    }

    @Test
    void stkPush_success_fetchesTokenAndReturnsParsedResult() {
        StkPushResult result = newClient().stkPush(sampleRequest());

        assertTrue(result.isAccepted());
        assertEquals("ws_CO_191220191020363925", result.getCheckoutRequestId());
        assertEquals("29115-34620561-1", result.getMerchantRequestId());
        assertEquals(1, tokenRequests.get());
    }

    @Test
    void stkPush_calledTwice_reusesCachedToken() {
        Daraja4jClient client = newClient();
        StkPushRequest request = sampleRequest();

        client.stkPush(request);
        client.stkPush(request);

        assertEquals(1, tokenRequests.get());
    }

    @Test
    void stkPush_embeddedFailureResponseCode_throwsApiException() {
        stkResponseBody = "{\"ResponseCode\":\"1\",\"ResponseDescription\":\"Unable to lock subscriber\"}";

        Daraja4jApiException exception = assertThrows(Daraja4jApiException.class, () -> newClient().stkPush(sampleRequest()));
        assertEquals("1", exception.getErrorCode());
    }

    @Test
    void stkPush_httpErrorStatus_throwsApiExceptionWithParsedErrorFields() {
        stkResponseStatus = 400;
        stkResponseBody = "{\"requestId\":\"1c5b-4ba8-815c-ac45c57a3db01495926\","
                + "\"errorCode\":\"400.002.02\",\"errorMessage\":\"Bad Request - Invalid BusinessShortCode\"}";

        Daraja4jApiException exception = assertThrows(Daraja4jApiException.class, () -> newClient().stkPush(sampleRequest()));
        assertEquals(400, exception.getHttpStatus());
        assertEquals("400.002.02", exception.getErrorCode());
        assertEquals("Bad Request - Invalid BusinessShortCode", exception.getErrorMessage());
    }

    @Test
    void concurrentCalls_shareOneTokenFetch() throws InterruptedException {
        Daraja4jClient client = newClient();
        StkPushRequest request = sampleRequest();
        int threadCount = 8;
        ExecutorService pool = Executors.newFixedThreadPool(threadCount);
        CountDownLatch ready = new CountDownLatch(threadCount);
        CountDownLatch start = new CountDownLatch(1);
        CountDownLatch done = new CountDownLatch(threadCount);

        for (int i = 0; i < threadCount; i++) {
            pool.submit(() -> {
                ready.countDown();
                try {
                    start.await();
                    client.stkPush(request);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    done.countDown();
                }
            });
        }

        ready.await();
        start.countDown();
        assertTrue(done.await(10, TimeUnit.SECONDS), "Concurrent stkPush calls did not complete in time");
        pool.shutdown();

        assertEquals(1, tokenRequests.get(), "Expected exactly one OAuth token fetch despite concurrent callers");
    }

    @Test
    void refreshToken_forcesANewFetchEvenWhenCachedTokenIsStillValid() {
        Daraja4jClient client = newClient();
        client.stkPush(sampleRequest());
        assertEquals(1, tokenRequests.get());

        client.refreshToken();
        client.stkPush(sampleRequest());

        assertEquals(2, tokenRequests.get());
    }
}
