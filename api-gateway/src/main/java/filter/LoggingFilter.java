package filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Component
@Slf4j
public class LoggingFilter implements GlobalFilter, Ordered {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String path = exchange.getRequest().getPath().toString();
        String url = exchange.getRequest().getURI().toString();
        LocalDateTime now = LocalDateTime.now();

        log.info("[{}] Incoming request to: {}", now, path);
        log.info("[{}] Full Request URL: {}", now, url);

        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return 1;
    }
}
