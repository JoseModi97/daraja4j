package io.github.josemodi97.daraja4j.jakarta;

import io.github.josemodi97.daraja4j.model.Daraja4jResultCallback;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Parses a {@code resultUrl} callback POSTed to a {@code jakarta.servlet}
 * endpoint - shared by B2C, B2B, B2Pochi, Reversal, Account Balance, and
 * Transaction Status, since they all use the same {@code Result} envelope.
 *
 * <pre>{@code
 * public class B2cResultServlet extends HttpServlet {
 *     private final Daraja4jResultCallbackServletHandler handler = new Daraja4jResultCallbackServletHandler()
 *             .onSuccess((result, req, res) -> markPayoutComplete(result.getTransactionId()))
 *             .onFailure((result, req, res) -> log.warn(result.getResultDesc()));
 *
 *     protected void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException {
 *         handler.handle(req, res);
 *     }
 * }
 * }</pre>
 */
public final class Daraja4jResultCallbackServletHandler {

    private Daraja4jServletCallback<Daraja4jResultCallback> onSuccess;
    private Daraja4jServletCallback<Daraja4jResultCallback> onFailure;

    public Daraja4jResultCallbackServletHandler onSuccess(Daraja4jServletCallback<Daraja4jResultCallback> callback) {
        this.onSuccess = callback;
        return this;
    }

    public Daraja4jResultCallbackServletHandler onFailure(Daraja4jServletCallback<Daraja4jResultCallback> callback) {
        this.onFailure = callback;
        return this;
    }

    public void handle(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String body = ServletRequestBodyReader.readBody(request);
        Daraja4jResultCallback result = Daraja4jResultCallback.parse(body);

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
