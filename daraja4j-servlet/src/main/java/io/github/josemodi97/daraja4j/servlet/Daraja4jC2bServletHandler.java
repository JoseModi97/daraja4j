package io.github.josemodi97.daraja4j.servlet;

import io.github.josemodi97.daraja4j.model.C2bConfirmation;
import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Parses a C2B confirmation POSTed to a {@code javax.servlet} endpoint - the
 * notification Daraja sends once a customer's payment to a registered
 * shortcode has completed. Unlike the other handlers, there's no
 * success/failure split: a confirmation is always a completed payment, so
 * there is a single {@link #onPayment(Daraja4jServletCallback)} callback.
 *
 * <p>This handler covers the {@code confirmationUrl} case only. If your
 * shortcode has External Validation enabled, build your {@code validationUrl}
 * servlet directly against {@link C2bConfirmation#parse(String)} - Daraja
 * expects an explicit accept/reject decision there, which doesn't fit this
 * always-acknowledge handler shape.
 *
 * <pre>{@code
 * public class C2bConfirmationServlet extends HttpServlet {
 *     private final Daraja4jC2bServletHandler handler = new Daraja4jC2bServletHandler()
 *             .onPayment((payment, req, res) -> creditAccount(payment.getBillRefNumber(), payment.getTransAmount()));
 *
 *     protected void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException {
 *         handler.handle(req, res);
 *     }
 * }
 * }</pre>
 */
public final class Daraja4jC2bServletHandler {

    private Daraja4jServletCallback<C2bConfirmation> onPayment;

    public Daraja4jC2bServletHandler onPayment(Daraja4jServletCallback<C2bConfirmation> callback) {
        this.onPayment = callback;
        return this;
    }

    public void handle(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String body = ServletRequestBodyReader.readBody(request);
        C2bConfirmation confirmation = C2bConfirmation.parse(body);

        if (onPayment != null) {
            onPayment.handle(confirmation, request, response);
        }

        if (!response.isCommitted()) {
            acknowledge(response);
        }
    }

    private static void acknowledge(HttpServletResponse response) throws IOException {
        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write("{\"ResultCode\":0,\"ResultDesc\":\"Success\"}");
    }
}
