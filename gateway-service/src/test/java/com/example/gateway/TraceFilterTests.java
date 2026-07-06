package com.example.gateway;

import com.example.gateway.client.AccountClient;
import com.example.gateway.config.TraceFilter;
import com.example.gateway.dto.TransactionResponse;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import java.math.BigDecimal;
import java.time.Instant;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class TraceFilterTests {
    @Autowired MockMvc mockMvc;
    @MockBean AccountClient accountClient;

    @Test
    void responseIncludesTraceIdHeader() throws Exception {
        when(accountClient.apply(anyString(), any())).thenReturn(new TransactionResponse("evt-trace", "acct-trace", "CREDIT", new BigDecimal("10.00"), "USD", Instant.now(), new BigDecimal("10.00")));
        mockMvc.perform(post("/events")
                        .header(TraceFilter.TRACE_HEADER, "trace-test-123")
                        .contentType("application/json")
                        .content("{\"eventId\":\"evt-trace\",\"accountId\":\"acct-trace\",\"type\":\"CREDIT\",\"amount\":10.00,\"currency\":\"USD\",\"eventTimestamp\":\"2026-05-15T14:02:11Z\"}"))
                .andExpect(status().isCreated())
                .andExpect(header().string(TraceFilter.TRACE_HEADER, "trace-test-123"));
    }
}
