package com.fullstack.inventario.config;

import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.core.registry.RegistryEventConsumer;
import io.github.resilience4j.retry.RetryConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import java.time.Duration;

@Configuration
@Slf4j
public class ResilienceConfig {

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder
                .setConnectTimeout(Duration.ofSeconds(5))
                .setReadTimeout(Duration.ofSeconds(10))
                .build();
    }

    @Bean
    public RetryConfig productoRetryConfig() {
        return RetryConfig.custom()
                .maxAttempts(3)
                .waitDuration(Duration.ofMillis(1000))
                .retryOnResult(response -> response != null)
                .retryExceptions(Exception.class)
                .build();
    }

    @Bean
    public CircuitBreakerConfig productoCircuitBreakerConfig() {
        return CircuitBreakerConfig.custom()
                .failureRateThreshold(50.0f)
                .slowCallRateThreshold(50.0f)
                .slowCallDurationThreshold(Duration.ofSeconds(2))
                .waitDurationInOpenState(Duration.ofSeconds(10))
                .build();
    }

    @Bean
    public RegistryEventConsumer<io.github.resilience4j.circuitbreaker.CircuitBreaker> myCircuitBreakerRegistryEventConsumer() {
        return new RegistryEventConsumer<io.github.resilience4j.circuitbreaker.CircuitBreaker>() {
            @Override
            public void onEntryAddedEvent(io.github.resilience4j.core.registry.EntryAddedEvent<io.github.resilience4j.circuitbreaker.CircuitBreaker> entryAddedEvent) {
                log.info("CircuitBreaker {} registrado", entryAddedEvent.getAddedEntry().getName());
            }

            @Override
            public void onEntryRemovedEvent(io.github.resilience4j.core.registry.EntryRemovedEvent<io.github.resilience4j.circuitbreaker.CircuitBreaker> entryRemovedEvent) {
                log.info("CircuitBreaker {} removido", entryRemovedEvent.getRemovedEntry().getName());
            }

            @Override
            public void onEntryReplacedEvent(io.github.resilience4j.core.registry.EntryReplacedEvent<io.github.resilience4j.circuitbreaker.CircuitBreaker> entryReplacedEvent) {
                log.info("CircuitBreaker {} reemplazado", entryReplacedEvent.getNewEntry().getName());
            }
        };
    }
}

