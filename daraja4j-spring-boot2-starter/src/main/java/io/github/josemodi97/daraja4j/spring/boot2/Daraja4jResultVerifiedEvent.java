package io.github.josemodi97.daraja4j.spring.boot2;

import io.github.josemodi97.daraja4j.model.Daraja4jResultCallback;
import org.springframework.context.ApplicationEvent;

/**
 * Published when the auto-registered result callback endpoint (see
 * {@link Daraja4jProperties.Webhook#getResult()}) receives a successful
 * B2C/B2B/B2Pochi/Reversal/Balance/StatusQuery result.
 */
public class Daraja4jResultVerifiedEvent extends ApplicationEvent {

    private static final long serialVersionUID = 1L;

    private final Daraja4jResultCallback result;

    public Daraja4jResultVerifiedEvent(Object source, Daraja4jResultCallback result) {
        super(source);
        this.result = result;
    }

    public Daraja4jResultCallback getResult() {
        return result;
    }
}
