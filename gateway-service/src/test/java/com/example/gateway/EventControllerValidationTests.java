package com.example.gateway;

import com.example.gateway.client.AccountClient;
import com.example.gateway.dto.TransactionResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import java.math.BigDecimal;
import java.time.Instant;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class EventControllerValidationTests {
    @Autowired MockMvc mockMvc;
    @MockBean AccountClient accountClient;

    @Test
    void rejectsMissingRequiredFields() throws Exception {
        mockMvc.perform(post("/events")
                        .contentType("application/json")
                        .content("{}"))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(accountClient);
    }

    @Test
    void rejectsUnsupportedTransactionType() throws Exception {
        mockMvc.perform(post("/events")
                        .contentType("application/json")
                        .content("{\"eventId\":\"evt-invalid-type\",\"accountId\":\"acct\",\"type\":\"UNKNOWN\",\"amount\":25.00,\"currency\":\"USD\",\"eventTimestamp\":\"2026-05-15T14:02:11Z\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("type must be CREDIT or DEBIT"));

        verifyNoInteractions(accountClient);
    }

    @Test
    void rejectsNonPositiveAmount() throws Exception {
        mockMvc.perform(post("/events")
                        .contentType("application/json")
                        .content("{\"eventId\":\"evt-invalid-amount\",\"accountId\":\"acct\",\"type\":\"CREDIT\",\"amount\":0,\"currency\":\"USD\",\"eventTimestamp\":\"2026-05-15T14:02:11Z\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("amount must be greater than 0"));

        verifyNoInteractions(accountClient);
    }

    @Test
    void acceptsValidPayload() throws Exception {
        when(accountClient.apply(anyString(), any()))
                .thenReturn(new TransactionResponse("evt-valid", "acct", "CREDIT", new BigDecimal("25.00"), "USD", Instant.parse("2026-05-15T14:02:11Z"), new BigDecimal("25.00")));

        mockMvc.perform(post("/events")
                        .contentType("application/json")
                        .content("{\"eventId\":\"evt-valid\",\"accountId\":\"acct\",\"type\":\"CREDIT\",\"amount\":25.00,\"currency\":\"USD\",\"eventTimestamp\":\"2026-05-15T14:02:11Z\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.eventId").value("evt-valid"))
                .andExpect(jsonPath("$.status").value("APPLIED"));

        verify(accountClient).apply(anyString(), any());
    }
}
