package com.example.gateway.config;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import java.io.IOException;
import java.util.UUID;

@Component
public class TraceFilter implements Filter {
    public static final String TRACE_HEADER = "X-Trace-ID";
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        String traceId = httpRequest.getHeader(TRACE_HEADER);
        if (traceId == null || traceId.isBlank()) traceId = UUID.randomUUID().toString();
        MDC.put("traceId", traceId);
        httpResponse.setHeader(TRACE_HEADER, traceId);
        try { chain.doFilter(request, response); } finally { MDC.clear(); }
    }
}
