package com.example.gateway.config;

import feign.RequestInterceptor;
import feign.Request;
import org.slf4j.MDC;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.util.concurrent.TimeUnit;

@Configuration
public class FeignConfig {
    @Bean
    RequestInterceptor traceRequestInterceptor() {
        return template -> {
            String traceId = MDC.get("traceId");
            if (traceId != null) template.header(TraceFilter.TRACE_HEADER, traceId);
        };
    }

    @Bean
    Request.Options feignOptions() {
        return new Request.Options(1, TimeUnit.SECONDS, 2, TimeUnit.SECONDS, true);
    }
}
