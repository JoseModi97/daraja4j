package io.github.josemodi97.daraja4j.spring.boot2;

import io.github.josemodi97.daraja4j.model.StkCallbackResult;
import org.springframework.context.ApplicationEvent;

/**
 * Published when the auto-registered STK callback endpoint (see
 * {@link Daraja4jProperties.Webhook#getStk()}) receives a successful
 * payment callback.
 *
 * <pre>{@code
 * @EventListener
 * void onPaid(Daraja4jStkPaymentVerifiedEvent event) {
 *     orderService.markPaid(event.getResult().getCheckoutRequestId(), event.getResult().getMpesaReceiptNumber());
 * }
 * }</pre>
 */
public class Daraja4jStkPaymentVerifiedEvent extends ApplicationEvent {

    private static final long serialVersionUID = 1L;

    private final StkCallbackResult result;

    public Daraja4jStkPaymentVerifiedEvent(Object source, StkCallbackResult result) {
        super(source);
        this.result = result;
    }

    public StkCallbackResult getResult() {
        return result;
    }
}
