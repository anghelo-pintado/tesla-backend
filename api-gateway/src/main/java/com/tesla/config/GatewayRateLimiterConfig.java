package com.tesla.config;

import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.cloud.gateway.filter.ratelimit.RedisRateLimiter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Mono;

@Configuration
public class GatewayRateLimiterConfig {

    // Limita por IP del cliente
    @Bean
    public KeyResolver ipKeyResolver() {
        return exchange -> Mono.justOrEmpty(
                        exchange.getRequest().getRemoteAddress()
                ).map(addr -> addr.getAddress().getHostAddress())
                .defaultIfEmpty("unknown");
    }

    // 20 peticiones por segundo, con burst de hasta 40
    @Bean
    public RedisRateLimiter redisRateLimiter() {
        return new RedisRateLimiter(20, 40, 1);
    }
}