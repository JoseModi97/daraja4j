package io.github.josemodi97.daraja4j.spring.boot2;

import io.github.josemodi97.daraja4j.Daraja4jClient;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Auto-configures a {@link Daraja4jClient} bean from {@code daraja4j.*}
 * properties, and (each opt-in independently) up to three webhook endpoints
 * that republish parsed callbacks as application events.
 *
 * <p>Only activates once {@code daraja4j.consumer-key} is set, so an
 * unconfigured app (or a test slice that doesn't need it) doesn't fail to
 * start just because this starter is on the classpath.
 */
@Configuration
@EnableConfigurationProperties(Daraja4jProperties.class)
@ConditionalOnClass(Daraja4jClient.class)
@ConditionalOnProperty(prefix = "daraja4j", name = "consumer-key")
public class Daraja4jAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public Daraja4jClient daraja4jClient(Daraja4jProperties properties) {
        return new Daraja4jClient(properties.toConfig());
    }

    @Configuration
    @ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
    @ConditionalOnClass(name = "org.springframework.web.bind.annotation.RestController")
    @ConditionalOnProperty(prefix = "daraja4j.webhook.stk", name = "enabled", havingValue = "true")
    static class StkWebhookConfiguration {

        @Bean
        @ConditionalOnMissingBean
        public Daraja4jStkWebhookController daraja4jStkWebhookController(ApplicationEventPublisher events) {
            return new Daraja4jStkWebhookController(events);
        }
    }

    @Configuration
    @ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
    @ConditionalOnClass(name = "org.springframework.web.bind.annotation.RestController")
    @ConditionalOnProperty(prefix = "daraja4j.webhook.result", name = "enabled", havingValue = "true")
    static class ResultWebhookConfiguration {

        @Bean
        @ConditionalOnMissingBean
        public Daraja4jResultWebhookController daraja4jResultWebhookController(ApplicationEventPublisher events) {
            return new Daraja4jResultWebhookController(events);
        }
    }

    @Configuration
    @ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
    @ConditionalOnClass(name = "org.springframework.web.bind.annotation.RestController")
    @ConditionalOnProperty(prefix = "daraja4j.webhook.c2b", name = "enabled", havingValue = "true")
    static class C2bWebhookConfiguration {

        @Bean
        @ConditionalOnMissingBean
        public Daraja4jC2bWebhookController daraja4jC2bWebhookController(ApplicationEventPublisher events) {
            return new Daraja4jC2bWebhookController(events);
        }
    }
}
