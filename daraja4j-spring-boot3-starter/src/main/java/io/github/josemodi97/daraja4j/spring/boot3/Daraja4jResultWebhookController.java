package io.github.josemodi97.daraja4j.spring.boot3;

import io.github.josemodi97.daraja4j.model.Daraja4jResultCallback;
import java.util.Collections;
import java.util.Map;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * Auto-registered when {@code daraja4j.webhook.result.enabled=true}: parses
 * an inbound B2C/B2B/B2Pochi/Reversal/Balance/StatusQuery result callback and
 * republishes it as a {@link Daraja4jResultVerifiedEvent} /
 * {@link Daraja4jResultFailedEvent}.
 */
@RestController
public class Daraja4jResultWebhookController {

    private final ApplicationEventPublisher events;

    public Daraja4jResultWebhookController(ApplicationEventPublisher events) {
        this.events = events;
    }

    @PostMapping("${daraja4j.webhook.result.path:/daraja4j/result-callback}")
    public ResponseEntity<Map<String, Object>> resultCallback(@RequestBody String rawBody) {
        Daraja4jResultCallback result = Daraja4jResultCallback.parse(rawBody);

        if (result.isSuccess()) {
            events.publishEvent(new Daraja4jResultVerifiedEvent(this, result));
        } else {
            events.publishEvent(new Daraja4jResultFailedEvent(this, result));
        }

        return ResponseEntity.ok(Collections.singletonMap("ResultCode", 0));
    }
}
