package com.example.gateway.filter;

import com.alibaba.csp.sentinel.Tracer;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
@Order(-1) // 优先级要高于 Sentinel 过滤器
public class SentinelExceptionMarkFilter implements GlobalFilter {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        return chain.filter(exchange).then(Mono.fromRunnable(() -> {
            HttpStatus statusCode = (HttpStatus) exchange.getResponse().getStatusCode();
            if (statusCode != null && statusCode.is5xxServerError()) {
                // 手动标记异常，让 Sentinel 统计到
                Tracer.trace(new RuntimeException("下游服务返回 " + statusCode.value()));
            }
        }));
    }
}