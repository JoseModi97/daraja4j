package io.github.josemodi97.daraja4j.spring.boot2;

import io.github.josemodi97.daraja4j.model.C2bConfirmation;
import org.springframework.context.ApplicationEvent;

/**
 * Published when the auto-registered C2B confirmation endpoint (see
 * {@link Daraja4jProperties.Webhook#getC2b()}) receives a completed payment.
 * There is no failure variant - a confirmation is only ever sent for a
 * completed payment.
 */
public class Daraja4jC2bPaymentReceivedEvent extends ApplicationEvent {

    private static final long serialVersionUID = 1L;

    private final C2bConfirmation confirmation;

    public Daraja4jC2bPaymentReceivedEvent(Object source, C2bConfirmation confirmation) {
        super(source);
        this.confirmation = confirmation;
    }

    public C2bConfirmation getConfirmation() {
        return confirmation;
    }
}
