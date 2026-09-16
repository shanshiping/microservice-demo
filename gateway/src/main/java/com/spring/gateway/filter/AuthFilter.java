package com.spring.gateway.filter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.List;

@Component
public class AuthFilter implements GlobalFilter, Ordered {

    // JWT 密钥，实际项目应该从配置中心读取
    private static final String SECRET_KEY = "your-secret-key-must-be-at-least-256-bits-long!!";
    private static final Key KEY = Keys.hmacShaKeyFor(SECRET_KEY.getBytes(StandardCharsets.UTF_8));

    // 白名单路径，不需要鉴权
    private static final List<String> WHITE_LIST = List.of(
            "/api/user/login",
            "/api/user/register",
            "/actuator"
//            "/test"
    );

        @Override
        public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
            ServerHttpRequest request = exchange.getRequest();
            String path = request.getURI().getPath();
            System.out.println(">>> [AuthFilter] 请求路径: " + path);

            // 白名单放行
            if (WHITE_LIST.stream().anyMatch(path::startsWith)) {
                System.out.println(">>> [AuthFilter] 白名单路径，放行");
                return chain.filter(exchange);
            }

            // 获取 Token
            String token = request.getHeaders().getFirst("Authorization");
            System.out.println(">>> [AuthFilter] 当前使用的密钥: [" + SECRET_KEY + "]");
            System.out.println(">>> [AuthFilter] 密钥长度: " + SECRET_KEY.length());
            System.out.println(">>> [AuthFilter] Authorization 头: " + (token == null ? "null" : token.substring(0, Math.min(20, token.length())) + "..."));

            if (token == null || !token.startsWith("Bearer ")) {
                System.out.println(">>> [AuthFilter] Token 为空或格式错误");
                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                return exchange.getResponse().setComplete();
            }

            // 解析 Token
            try {
                String jwt = token.substring(7);
                System.out.println(">>> [AuthFilter] JWT 长度: " + jwt.length());

                Claims claims = Jwts.parserBuilder()
                        .setSigningKey(SECRET_KEY.getBytes(StandardCharsets.UTF_8))
                        .build()
                        .parseClaimsJws(jwt)
                        .getBody();

                String userId = claims.getSubject();
                System.out.println(">>> [AuthFilter] Token 解析成功, userId: " + userId);

                ServerHttpRequest mutatedRequest = request.mutate()
                        .header("X-User-Id", userId)
                        .build();
                return chain.filter(exchange.mutate().request(mutatedRequest).build());
            } catch (Exception e) {
                System.out.println(">>> [AuthFilter] Token 解析失败: " + e.getClass().getSimpleName() + " - " + e.getMessage());
                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                return exchange.getResponse().setComplete();
            }
        }

        @Override
        public int getOrder() {
            return -100;
    }
}