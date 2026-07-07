package com.example.gateway.controller;

import com.example.gateway.dto.*;
import com.example.gateway.service.EventService;
import com.example.gateway.service.AuditService;
import com.example.gateway.entity.AuditRecord;
import jakarta.validation.Valid;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import javax.sql.DataSource;
import java.sql.Connection;
import java.util.*;

@RestController
public class EventController {
    private final EventService eventService;
    private final AuditService auditService;
    private final DataSource dataSource;
    public EventController(EventService eventService, AuditService auditService, DataSource dataSource) { this.eventService = eventService; this.auditService = auditService; this.dataSource = dataSource; }

    @PostMapping("/events")
    public ResponseEntity<EventResponse> create(@Valid @RequestBody EventRequest request) {
        EventResponse response = eventService.create(request);
        HttpStatus status = "APPLIED".equals(response.status()) ? HttpStatus.CREATED : HttpStatus.OK;
        return ResponseEntity.status(status).body(response);
    }

    @GetMapping("/events/{id}")
    public EventResponse get(@PathVariable String id) { return eventService.get(id); }

    @GetMapping(value = "/events", params = "account")
    public List<EventResponse> list(@RequestParam("account") String accountId) { return eventService.list(accountId); }

    @GetMapping("/accounts/{accountId}/balance")
    public Map<String,Object> balance(@PathVariable String accountId) { return eventService.balance(accountId); }

    @GetMapping("/audit")
    public List<AuditRecord> latestAudit() { return auditService.latest(); }

    @GetMapping("/events/{id}/audit")
    public List<AuditRecord> eventAudit(@PathVariable String id) { return auditService.byEvent(id); }

    @GetMapping("/health")
    public Map<String,Object> health() {
        try (Connection ignored = dataSource.getConnection()) { return Map.of("service", "gateway-service", "status", "UP", "database", "CONNECTED"); }
        catch (Exception e) { return Map.of("service", "gateway-service", "status", "DOWN", "database", "DISCONNECTED"); }
    }
}
