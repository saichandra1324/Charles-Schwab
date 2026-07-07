package com.example.gateway;

import com.example.gateway.client.AccountClient;
import com.example.gateway.dto.*;
import com.example.gateway.exception.AccountServiceUnavailableException;
import com.example.gateway.exception.EventNotFoundException;
import com.example.gateway.service.EventService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Map;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@SpringBootTest(properties = "account-service.url=http://localhost:9999")
class EventServiceTests {
    @Autowired EventService eventService;
    @MockBean AccountClient accountClient;

    @Test
    void duplicateEventDoesNotCallAccountServiceAgain() {
        EventRequest request = sample("evt-g1", "2026-05-15T12:00:00Z");
        when(accountClient.apply(anyString(), any(TransactionRequest.class)))
                .thenReturn(new TransactionResponse("evt-g1", "acct-g", "CREDIT", new BigDecimal("50.00"), "USD", Instant.now(), new BigDecimal("50.00")));
        EventCreationResult first = eventService.create(request);
        EventCreationResult duplicate = eventService.create(request);

        assertThat(first.created()).isTrue();
        assertThat(duplicate.created()).isFalse();
        assertThat(duplicate.response().eventId()).isEqualTo("evt-g1");
        verify(accountClient, times(1)).apply(anyString(), any(TransactionRequest.class));
    }

    @Test
    void listEventsOrderedByEventTimestamp() {
        when(accountClient.apply(anyString(), any(TransactionRequest.class))).thenReturn(null);
        eventService.create(sample("evt-g2", "2026-05-15T13:00:00Z"));
        eventService.create(sample("evt-g3", "2026-05-15T10:00:00Z"));
        var events = eventService.list("acct-g");
        assertThat(events).extracting(EventResponse::eventId).containsSubsequence("evt-g3", "evt-g2");
    }

    @Test
    void accountServiceFailureIsTranslatedToServiceUnavailable() {
        when(accountClient.apply(anyString(), any(TransactionRequest.class))).thenThrow(new RuntimeException("down"));
        assertThatThrownBy(() -> eventService.create(sample("evt-g4", "2026-05-15T14:00:00Z")))
                .isInstanceOf(AccountServiceUnavailableException.class);
    }

    @Test
    void missingEventLookupThrowsNotFound() {
        assertThatThrownBy(() -> eventService.get("evt-does-not-exist"))
                .isInstanceOf(EventNotFoundException.class)
                .hasMessageContaining("evt-does-not-exist");
    }

    @Test
    void balanceQueryFailureIsTranslatedToServiceUnavailable() {
        when(accountClient.balance("acct-balance-down")).thenThrow(new RuntimeException("down"));

        assertThatThrownBy(() -> eventService.balance("acct-balance-down"))
                .isInstanceOf(AccountServiceUnavailableException.class)
                .hasMessageContaining("Account Service is unreachable for balance query");
    }

    private EventRequest sample(String eventId, String timestamp) {
        return new EventRequest(eventId, "acct-g", "CREDIT", new BigDecimal("50.00"), "USD", Instant.parse(timestamp), Map.of("source", "test"));
    }
}
