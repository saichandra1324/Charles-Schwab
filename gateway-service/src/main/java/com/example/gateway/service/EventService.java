package com.example.gateway.service;

import com.example.gateway.client.AccountClient;
import com.example.gateway.dto.*;
import com.example.gateway.entity.EventRecord;
import com.example.gateway.exception.*;
import com.example.gateway.repository.EventRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import io.micrometer.core.instrument.MeterRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class EventService {
    private static final Logger log = LoggerFactory.getLogger(EventService.class);
    private final EventRepository eventRepository;
    private final AccountClient accountClient;
    private final ObjectMapper objectMapper;
    private final MeterRegistry meterRegistry;
    private final AuditService auditService;

    public EventService(EventRepository eventRepository, AccountClient accountClient, ObjectMapper objectMapper, MeterRegistry meterRegistry, AuditService auditService) {
        this.eventRepository = eventRepository; this.accountClient = accountClient; this.objectMapper = objectMapper; this.meterRegistry = meterRegistry; this.auditService = auditService;
    }

    @Transactional(noRollbackFor = AccountServiceUnavailableException.class)
    public EventCreationResult create(EventRequest request) {
        Optional<EventRecord> existing = eventRepository.findById(request.eventId());
        if (existing.isPresent()) {
            log.info("Duplicate event submission ignored eventId={}", request.eventId());
            auditService.record("EVENT_DUPLICATE", request.eventId(), request.accountId(), "IGNORED", "Duplicate event returned without changing balance");
            meterRegistry.counter("gateway.events.duplicate").increment();
            return new EventCreationResult(toResponse(existing.get()), false);
        }
        String metadataJson = writeMetadata(request.metadata());
        EventRecord saved = eventRepository.save(new EventRecord(request.eventId(), request.accountId(), request.type(), request.amount(), request.currency(), request.eventTimestamp(), metadataJson, "RECEIVED"));
        auditService.record("EVENT_RECEIVED", request.eventId(), request.accountId(), "SUCCESS", "Event accepted by gateway and stored locally");
        try {
            applyTransaction(request);
            saved.setStatus("APPLIED");
            meterRegistry.counter("gateway.events.created", "status", "APPLIED").increment();
            log.info("Event processed eventId={} accountId={}", request.eventId(), request.accountId());
            auditService.record("EVENT_APPLIED", request.eventId(), request.accountId(), "SUCCESS", "Account service applied the transaction");
            return new EventCreationResult(toResponse(saved), true);
        } catch (Exception ex) {
            saved.setStatus("FAILED_ACCOUNT_SERVICE_UNAVAILABLE");
            meterRegistry.counter("gateway.events.created", "status", "FAILED").increment();
            log.error("Account service unavailable eventId={} accountId={}", request.eventId(), request.accountId(), ex);
            auditService.record("EVENT_APPLY_FAILED", request.eventId(), request.accountId(), "FAILED", "Account service unavailable while applying transaction");
            if (ex instanceof AccountServiceUnavailableException unavailable) {
                throw unavailable;
            }
            throw new AccountServiceUnavailableException("Account Service is currently unavailable. Please retry later.");
        }
    }

    @Retry(name = "accountService")
    @CircuitBreaker(name = "accountService", fallbackMethod = "applyFallback")
    public TransactionResponse applyTransaction(EventRequest request) {
        return accountClient.apply(request.accountId(), new TransactionRequest(request.eventId(), request.accountId(), request.type(), request.amount(), request.currency(), request.eventTimestamp()));
    }

    public TransactionResponse applyFallback(EventRequest request, Throwable ex) {
        throw new AccountServiceUnavailableException("Account Service is currently unavailable. Please retry later.");
    }

    @Transactional(readOnly = true)
    public EventResponse get(String id) { return eventRepository.findById(id).map(this::toResponse).orElseThrow(() -> new EventNotFoundException(id)); }

    @Transactional(readOnly = true)
    public List<EventResponse> list(String accountId) { return eventRepository.findByAccountIdOrderByEventTimestampAsc(accountId).stream().map(this::toResponse).toList(); }

    public Map<String,Object> balance(String accountId) {
        try { return accountClient.balance(accountId); } catch (Exception e) { throw new AccountServiceUnavailableException("Account Service is unreachable for balance query"); }
    }

    private String writeMetadata(Map<String,Object> metadata) {
        try { return objectMapper.writeValueAsString(metadata == null ? Map.of() : metadata); }
        catch (Exception e) { throw new IllegalArgumentException("Invalid metadata"); }
    }

    private EventResponse toResponse(EventRecord e) {
        Map<String,Object> metadata;
        try { metadata = objectMapper.readValue(e.getMetadataJson() == null ? "{}" : e.getMetadataJson(), new TypeReference<>() {}); }
        catch (Exception ex) { metadata = Map.of(); }
        return new EventResponse(e.getEventId(), e.getAccountId(), e.getType(), e.getAmount(), e.getCurrency(), e.getEventTimestamp(), metadata, e.getStatus(), e.getCreatedAt());
    }
}
