package io.github.josemodi97.daraja4j.spring.boot2;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

class Daraja4jStkWebhookControllerTest {

    private static final String SUCCESS_BODY = "{\"Body\":{\"stkCallback\":{"
            + "\"MerchantRequestID\":\"29115-34620561-1\","
            + "\"CheckoutRequestID\":\"ws_CO_191220191020363925\","
            + "\"ResultCode\":0,"
            + "\"ResultDesc\":\"The service request is processed successfully.\""
            + "}}}";

    private static final String FAILURE_BODY = "{\"Body\":{\"stkCallback\":{"
            + "\"MerchantRequestID\":\"f1e2-4b95-a71d-b30d3cdbb7a7942864\","
            + "\"CheckoutRequestID\":\"ws_CO_21072024125243250722943992\","
            + "\"ResultCode\":1032,"
            + "\"ResultDesc\":\"Request cancelled by user\""
            + "}}}";

    @Test
    void publishesVerifiedEventForASuccessfulCallback() {
        ApplicationEventPublisher events = mock(ApplicationEventPublisher.class);
        Daraja4jStkWebhookController controller = new Daraja4jStkWebhookController(events);

        ResponseEntity<Map<String, Object>> response = controller.stkCallback(SUCCESS_BODY);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        verify(events).publishEvent(any(Daraja4jStkPaymentVerifiedEvent.class));
    }

    @Test
    void publishesFailedEventForACancelledCallback() {
        ApplicationEventPublisher events = mock(ApplicationEventPublisher.class);
        Daraja4jStkWebhookController controller = new Daraja4jStkWebhookController(events);

        ResponseEntity<Map<String, Object>> response = controller.stkCallback(FAILURE_BODY);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        verify(events).publishEvent(any(Daraja4jStkPaymentFailedEvent.class));
    }
}
