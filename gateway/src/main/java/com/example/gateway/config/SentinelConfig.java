package com.example.gateway.config;

import com.alibaba.csp.sentinel.adapter.gateway.sc.callback.BlockRequestHandler;
import com.alibaba.csp.sentinel.adapter.gateway.sc.callback.GatewayCallbackManager;
import org.springframework.context.annotation.Configuration;
import javax.annotation.PostConstruct;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
public class SentinelConfig {

    @PostConstruct
    public void init() {
        BlockRequestHandler blockRequestHandler = (exchange, t) -> {
            return ServerResponse.status(HttpStatus.TOO_MANY_REQUESTS)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(BodyInserters.fromValue(
                            "{\"code\":429,\"message\":\"请求太频繁，请稍后再试\"}"
                    ));
        };
        GatewayCallbackManager.setBlockHandler(blockRequestHandler);
    }
}