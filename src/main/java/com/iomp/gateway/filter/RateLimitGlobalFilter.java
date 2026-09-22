package com.iomp.gateway.filter;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import com.iomp.gateway.service.RateLimitService;

import lombok.extern.log4j.Log4j2;
import reactor.core.publisher.Mono;

@Component
@Log4j2
public class RateLimitGlobalFilter implements GlobalFilter, Ordered {

    private final RateLimitService rateLimitService;

    public RateLimitGlobalFilter(RateLimitService rateLimitService) {
        this.rateLimitService = rateLimitService;
    }

    @Override
    public Mono<Void> filter(
            ServerWebExchange exchange,
            GatewayFilterChain chain) {
    	
    	if (exchange.getRequest().getURI().getPath()
    	        .startsWith("/api/auth/")) {
    	    return chain.filter(exchange);
    	}

    	return exchange.getPrincipal()
    	        .map(principal -> principal.getName())
    	        .flatMap(username ->
    	                rateLimitService.isAllowed(username)
    	                        .flatMap(allowed -> {

    	                            if (allowed) {
    	                                return chain.filter(exchange);
    	                            }
    	                            log.warn(
    	                            	    "Rate limit exceeded: username={}, path={}",
    	                            	    username,
    	                            	    exchange.getRequest().getURI().getPath()
    	                            	);

    	                            exchange.getResponse()
    	                                    .setStatusCode(
    	                                            HttpStatus.TOO_MANY_REQUESTS
    	                                    );

    	                            return exchange.getResponse()
    	                                    .setComplete();
    	                        })
    	        );
    }

    @Override
    public int getOrder() {
        return 0;
    }
}