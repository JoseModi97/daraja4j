package io.github.josemodi97.daraja4j.spring.boot3;

import io.github.josemodi97.daraja4j.model.Daraja4jResultCallback;
import org.springframework.context.ApplicationEvent;

/** Published when the auto-registered result callback endpoint receives a failed result. */
public class Daraja4jResultFailedEvent extends ApplicationEvent {

    private static final long serialVersionUID = 1L;

    private final Daraja4jResultCallback result;

    public Daraja4jResultFailedEvent(Object source, Daraja4jResultCallback result) {
        super(source);
        this.result = result;
    }

    public Daraja4jResultCallback getResult() {
        return result;
    }
}
