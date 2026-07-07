package com.example.gateway;

import com.example.gateway.client.AccountClient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class EventControllerValidationTests {
    @Autowired MockMvc mockMvc;
    @MockBean AccountClient accountClient;

    @Test
    void rejectsInvalidPayload() throws Exception {
        mockMvc.perform(post("/events")
                        .contentType("application/json")
                        .content("{\"eventId\":\"evt-invalid\",\"accountId\":\"acct\",\"type\":\"UNKNOWN\",\"amount\":0,\"currency\":\"USD\",\"eventTimestamp\":\"2026-05-15T14:02:11Z\"}"))
                .andExpect(status().isBadRequest());
    }
}
