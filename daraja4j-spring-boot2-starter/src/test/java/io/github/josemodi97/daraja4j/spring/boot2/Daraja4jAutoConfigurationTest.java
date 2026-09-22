package io.github.josemodi97.daraja4j.spring.boot2;

import static org.assertj.core.api.Assertions.assertThat;

import io.github.josemodi97.daraja4j.Daraja4jClient;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

class Daraja4jAutoConfigurationTest {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(Daraja4jAutoConfiguration.class));

    @Test
    void registersAClientBeanWhenCredentialsAreConfigured() {
        contextRunner
                .withPropertyValues(
                        "daraja4j.consumer-key=KEY1",
                        "daraja4j.consumer-secret=SECRET1",
                        "daraja4j.environment=SANDBOX")
                .run(context -> {
                    assertThat(context).hasSingleBean(Daraja4jClient.class);
                    Daraja4jClient client = context.getBean(Daraja4jClient.class);
                    assertThat(client.getGateway().getConfig().getConsumerKey()).isEqualTo("KEY1");
                });
    }

    @Test
    void doesNotRegisterAClientBeanWhenUnconfigured() {
        contextRunner.run(context -> assertThat(context).doesNotHaveBean(Daraja4jClient.class));
    }

    @Test
    void bindsShortcodeAndWebhookProperties() {
        contextRunner
                .withPropertyValues(
                        "daraja4j.consumer-key=KEY1",
                        "daraja4j.consumer-secret=SECRET1",
                        "daraja4j.environment=SANDBOX",
                        "daraja4j.shortcode=174379",
                        "daraja4j.webhook.stk.enabled=true",
                        "daraja4j.webhook.stk.path=/custom/stk")
                .run(context -> {
                    Daraja4jProperties properties = context.getBean(Daraja4jProperties.class);
                    assertThat(properties.getShortcode()).isEqualTo("174379");
                    assertThat(properties.getWebhook().getStk().isEnabled()).isTrue();
                    assertThat(properties.getWebhook().getStk().getPath()).isEqualTo("/custom/stk");
                    assertThat(properties.getWebhook().getResult().isEnabled()).isFalse();
                });
    }

    @Test
    void environmentPropertyIsCaseInsensitive() {
        contextRunner
                .withPropertyValues(
                        "daraja4j.consumer-key=KEY1",
                        "daraja4j.consumer-secret=SECRET1",
                        "daraja4j.environment=sandbox")
                .run(context -> {
                    Daraja4jClient client = context.getBean(Daraja4jClient.class);
                    assertThat(client.getGateway().getConfig().getEnvironment())
                            .isEqualTo(io.github.josemodi97.daraja4j.Daraja4jConfig.Environment.SANDBOX);
                });
    }
}
