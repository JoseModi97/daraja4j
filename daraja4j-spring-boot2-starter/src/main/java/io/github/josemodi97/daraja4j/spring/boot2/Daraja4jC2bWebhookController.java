package io.github.josemodi97.daraja4j.spring.boot2;

import io.github.josemodi97.daraja4j.model.C2bConfirmation;
import java.util.Collections;
import java.util.Map;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * Auto-registered when {@code daraja4j.webhook.c2b.enabled=true}: parses an
 * inbound C2B confirmation and republishes it as a
 * {@link Daraja4jC2bPaymentReceivedEvent}. Covers the {@code confirmationUrl}
 * case only - see {@link Daraja4jProperties.Webhook#getC2b()} javadoc.
 */
@RestController
public class Daraja4jC2bWebhookController {

    private final ApplicationEventPublisher events;

    public Daraja4jC2bWebhookController(ApplicationEventPublisher events) {
        this.events = events;
    }

    @PostMapping("${daraja4j.webhook.c2b.path:/daraja4j/c2b-confirmation}")
    public ResponseEntity<Map<String, Object>> c2bConfirmation(@RequestBody String rawBody) {
        C2bConfirmation confirmation = C2bConfirmation.parse(rawBody);
        events.publishEvent(new Daraja4jC2bPaymentReceivedEvent(this, confirmation));
        return ResponseEntity.ok(Collections.singletonMap("ResultCode", 0));
    }
}
