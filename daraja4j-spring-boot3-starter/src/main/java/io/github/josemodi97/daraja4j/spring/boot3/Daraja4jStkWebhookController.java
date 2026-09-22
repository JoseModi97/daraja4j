package io.github.josemodi97.daraja4j.spring.boot3;

import io.github.josemodi97.daraja4j.model.StkCallbackResult;
import java.util.Collections;
import java.util.Map;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * Auto-registered when {@code daraja4j.webhook.stk.enabled=true}: parses an
 * inbound STK Push callback and republishes it as a
 * {@link Daraja4jStkPaymentVerifiedEvent} / {@link Daraja4jStkPaymentFailedEvent}
 * so application code can react with {@code @EventListener} instead of
 * writing its own controller.
 */
@RestController
public class Daraja4jStkWebhookController {

    private final ApplicationEventPublisher events;

    public Daraja4jStkWebhookController(ApplicationEventPublisher events) {
        this.events = events;
    }

    @PostMapping("${daraja4j.webhook.stk.path:/daraja4j/stk-callback}")
    public ResponseEntity<Map<String, Object>> stkCallback(@RequestBody String rawBody) {
        StkCallbackResult result = StkCallbackResult.parse(rawBody);

        if (result.isSuccess()) {
            events.publishEvent(new Daraja4jStkPaymentVerifiedEvent(this, result));
        } else {
            events.publishEvent(new Daraja4jStkPaymentFailedEvent(this, result));
        }

        return ResponseEntity.ok(Collections.singletonMap("ResultCode", 0));
    }
}
