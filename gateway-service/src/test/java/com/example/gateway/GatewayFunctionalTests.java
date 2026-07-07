package com.example.gateway;

import com.example.gateway.client.AccountClient;
import com.example.gateway.dto.TransactionRequest;
import com.example.gateway.dto.TransactionResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.Instant;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class GatewayFunctionalTests {
    @Autowired MockMvc mockMvc;
    @MockBean AccountClient accountClient;

    @Test
    void createsAndRetrievesEventThroughHttpApi() throws Exception {
        when(accountClient.apply(anyString(), any(TransactionRequest.class)))
                .thenReturn(txResponse("evt-functional-1", "acct-functional", "CREDIT", "150.00", "150.00"));

        mockMvc.perform(post("/events")
                        .contentType("application/json")
                        .content("""
                                {
                                  "eventId": "evt-functional-1",
                                  "accountId": "acct-functional",
                                  "type": "CREDIT",
                                  "amount": 150.00,
                                  "currency": "USD",
                                  "eventTimestamp": "2026-05-15T14:02:11Z",
                                  "metadata": {"source": "functional-test"}
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.eventId").value("evt-functional-1"))
                .andExpect(jsonPath("$.status").value("APPLIED"));

        mockMvc.perform(get("/events/evt-functional-1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.eventId").value("evt-functional-1"))
                .andExpect(jsonPath("$.metadata.source").value("functional-test"));
    }

    @Test
    void duplicatePostReturnsExistingEventWithoutSecondAccountCall() throws Exception {
        when(accountClient.apply(anyString(), any(TransactionRequest.class)))
                .thenReturn(txResponse("evt-functional-dup", "acct-functional", "CREDIT", "40.00", "40.00"));

        String body = """
                {
                  "eventId": "evt-functional-dup",
                  "accountId": "acct-functional",
                  "type": "CREDIT",
                  "amount": 40.00,
                  "currency": "USD",
                  "eventTimestamp": "2026-05-15T14:02:11Z"
                }
                """;

        mockMvc.perform(post("/events").contentType("application/json").content(body))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/events").contentType("application/json").content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.eventId").value("evt-functional-dup"));

        verify(accountClient, times(1)).apply(anyString(), any(TransactionRequest.class));
    }

    @Test
    void accountServiceFailureReturnsServiceUnavailable() throws Exception {
        when(accountClient.apply(anyString(), any(TransactionRequest.class)))
                .thenThrow(new RuntimeException("simulated downstream outage"));

        mockMvc.perform(post("/events")
                        .contentType("application/json")
                        .content("""
                                {
                                  "eventId": "evt-downstream-failure",
                                  "accountId": "acct-downstream",
                                  "type": "DEBIT",
                                  "amount": 10.00,
                                  "currency": "USD",
                                  "eventTimestamp": "2026-05-15T14:02:11Z"
                                }
                                """))
                .andExpect(status().isServiceUnavailable())
                .andExpect(jsonPath("$.message").value("Account Service is currently unavailable. Please retry later."));
    }

    @Test
    void localEventReadWorksAfterDownstreamFailure() throws Exception {
        when(accountClient.apply(anyString(), any(TransactionRequest.class)))
                .thenThrow(new RuntimeException("simulated downstream outage"));

        mockMvc.perform(post("/events")
                        .contentType("application/json")
                        .content("""
                                {
                                  "eventId": "evt-local-after-failure",
                                  "accountId": "acct-local-read",
                                  "type": "CREDIT",
                                  "amount": 25.00,
                                  "currency": "USD",
                                  "eventTimestamp": "2026-05-15T10:00:00Z"
                                }
                                """))
                .andExpect(status().isServiceUnavailable());

        mockMvc.perform(get("/events/evt-local-after-failure"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.eventId").value("evt-local-after-failure"))
                .andExpect(jsonPath("$.status").value("FAILED_ACCOUNT_SERVICE_UNAVAILABLE"));
    }

    @Test
    void auditEndpointReturnsEventAudit() throws Exception {
        when(accountClient.apply(anyString(), any(TransactionRequest.class)))
                .thenReturn(txResponse("evt-audit-functional", "acct-audit-functional", "CREDIT", "12.00", "12.00"));

        mockMvc.perform(post("/events")
                        .contentType("application/json")
                        .content("""
                                {
                                  "eventId": "evt-audit-functional",
                                  "accountId": "acct-audit-functional",
                                  "type": "CREDIT",
                                  "amount": 12.00,
                                  "currency": "USD",
                                  "eventTimestamp": "2026-05-15T14:02:11Z"
                                }
                                """))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/events/evt-audit-functional/audit"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].eventId").value("evt-audit-functional"));
    }

    private TransactionResponse txResponse(String eventId, String accountId, String type, String amount, String balance) {
        return new TransactionResponse(eventId, accountId, type, new BigDecimal(amount), "USD", Instant.parse("2026-05-15T14:02:11Z"), new BigDecimal(balance));
    }
}
