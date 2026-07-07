package com.example.gateway.config;
import feign.RequestTemplate;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.slf4j.MDC;

import static org.assertj.core.api.Assertions.assertThat;

class FeignConfigTests {
    private final FeignConfig feignConfig = new FeignConfig();

    @AfterEach
    void clearMdc() {
        MDC.clear();
    }

    @Test
    void propagatesTraceIdFromMdcToFeignHeader() {
        MDC.put("traceId", "trace-feign-123");
        RequestTemplate template = new RequestTemplate();

        feignConfig.traceRequestInterceptor().apply(template);

        assertThat(template.headers())
                .containsKey(TraceFilter.TRACE_HEADER);
        assertThat(template.headers().get(TraceFilter.TRACE_HEADER))
                .containsExactly("trace-feign-123");
    }

    @Test
    void doesNotAddTraceHeaderWhenTraceIdIsMissing() {
        RequestTemplate template = new RequestTemplate();

        feignConfig.traceRequestInterceptor().apply(template);

        assertThat(template.headers()).doesNotContainKey(TraceFilter.TRACE_HEADER);
    }
}
