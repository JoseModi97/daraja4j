package io.github.josemodi97.daraja4j.spring.boot2;

import io.github.josemodi97.daraja4j.model.StkCallbackResult;
import org.springframework.context.ApplicationEvent;

/** Published when the auto-registered STK callback endpoint receives a failed/cancelled payment callback. */
public class Daraja4jStkPaymentFailedEvent extends ApplicationEvent {

    private static final long serialVersionUID = 1L;

    private final StkCallbackResult result;

    public Daraja4jStkPaymentFailedEvent(Object source, StkCallbackResult result) {
        super(source);
        this.result = result;
    }

    public StkCallbackResult getResult() {
        return result;
    }
}
