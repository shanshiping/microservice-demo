package com.example.gateway.config;

import org.springframework.cloud.client.circuitbreaker.ReactiveCircuitBreakerFactory;
import org.springframework.cloud.gateway.filter.factory.SpringCloudCircuitBreakerFilterFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

@Configuration
public class SentinelCircuitBreakerConfig {

    @Bean
    public SpringCloudCircuitBreakerFilterFactory sentinelCircuitBreakerFilterFactory(
            ReactiveCircuitBreakerFactory circuitBreakerFactory) {
        return new SpringCloudCircuitBreakerFilterFactory(circuitBreakerFactory, null) {
            @Override
            protected Mono<Void> handleErrorWithoutFallback(Throwable ex, boolean streaming) {
                return Mono.error(new ResponseStatusException(
                        HttpStatus.SERVICE_UNAVAILABLE,
                        "Service unavailable, circuit breaker is open", ex));
            }
        };
    }
}
