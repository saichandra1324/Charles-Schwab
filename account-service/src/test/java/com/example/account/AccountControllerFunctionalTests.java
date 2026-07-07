package com.example.account;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class AccountControllerFunctionalTests {
    @Autowired MockMvc mockMvc;

    @Test
    void appliesTransactionAndReturnsBalanceThroughHttpApi() throws Exception {
        mockMvc.perform(post("/accounts/acct-controller/transactions")
                        .contentType("application/json")
                        .content("""
                                {
                                  "eventId": "evt-account-controller-1",
                                  "accountId": "acct-controller",
                                  "type": "CREDIT",
                                  "amount": 75.00,
                                  "currency": "USD",
                                  "eventTimestamp": "2026-05-15T14:02:11Z"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.eventId").value("evt-account-controller-1"))
                .andExpect(jsonPath("$.balance").value(75.00));

        mockMvc.perform(get("/accounts/acct-controller/balance"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accountId").value("acct-controller"))
                .andExpect(jsonPath("$.balance").value(75.00));
    }

    @Test
    void duplicateTransactionDoesNotChangeBalanceThroughHttpApi() throws Exception {
        String body = """
                {
                  "eventId": "evt-account-controller-dup",
                  "accountId": "acct-controller-dup",
                  "type": "CREDIT",
                  "amount": 30.00,
                  "currency": "USD",
                  "eventTimestamp": "2026-05-15T14:02:11Z"
                }
                """;

        mockMvc.perform(post("/accounts/acct-controller-dup/transactions").contentType("application/json").content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.balance").value(30.00));

        mockMvc.perform(post("/accounts/acct-controller-dup/transactions").contentType("application/json").content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.balance").value(30.00));
    }

    @Test
    void rejectsAccountIdMismatch() throws Exception {
        mockMvc.perform(post("/accounts/acct-path/transactions")
                        .contentType("application/json")
                        .content("""
                                {
                                  "eventId": "evt-mismatch",
                                  "accountId": "acct-body",
                                  "type": "CREDIT",
                                  "amount": 10.00,
                                  "currency": "USD",
                                  "eventTimestamp": "2026-05-15T14:02:11Z"
                                }
                                """))
                .andExpect(status().isBadRequest());
    }
}
