package io.github.josemodi97.daraja4j.jakarta;

import io.github.josemodi97.daraja4j.model.StkCallbackResult;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Parses an M-Pesa Express (STK Push) callback POSTed to a {@code jakarta.servlet}
 * endpoint (Tomcat 10+, Spring Boot 3, Jakarta EE 9+).
 *
 * <pre>{@code
 * public class StkCallbackServlet extends HttpServlet {
 *     private final Daraja4jStkCallbackServletHandler handler = new Daraja4jStkCallbackServletHandler()
 *             .onSuccess((result, req, res) -> markOrderPaid(result.getCheckoutRequestId(), result.getMpesaReceiptNumber()))
 *             .onFailure((result, req, res) -> log.warn(result.getResultDesc()));
 *
 *     protected void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException {
 *         handler.handle(req, res);
 *     }
 * }
 * }</pre>
 */
public final class Daraja4jStkCallbackServletHandler {

    private Daraja4jServletCallback<StkCallbackResult> onSuccess;
    private Daraja4jServletCallback<StkCallbackResult> onFailure;

    public Daraja4jStkCallbackServletHandler onSuccess(Daraja4jServletCallback<StkCallbackResult> callback) {
        this.onSuccess = callback;
        return this;
    }

    public Daraja4jStkCallbackServletHandler onFailure(Daraja4jServletCallback<StkCallbackResult> callback) {
        this.onFailure = callback;
        return this;
    }

    public void handle(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String body = ServletRequestBodyReader.readBody(request);
        StkCallbackResult result = StkCallbackResult.parse(body);

        if (result.isSuccess()) {
            if (onSuccess != null) {
                onSuccess.handle(result, request, response);
            }
        } else if (onFailure != null) {
            onFailure.handle(result, request, response);
        }

        if (!response.isCommitted()) {
            acknowledge(response);
        }
    }

    private static void acknowledge(HttpServletResponse response) throws IOException {
        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write("{\"ResultCode\":0,\"ResultDesc\":\"Accepted\"}");
    }
}
