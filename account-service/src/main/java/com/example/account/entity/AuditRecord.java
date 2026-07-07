package com.example.account.entity;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "audit_records", indexes = {
        @Index(name = "idx_account_audit_event", columnList = "eventId"),
        @Index(name = "idx_account_audit_trace", columnList = "traceId"),
        @Index(name = "idx_account_audit_created", columnList = "createdAt")
})
public class AuditRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String action;
    private String eventId;
    private String accountId;
    private String traceId;
    @Column(nullable = false)
    private String outcome;
    @Column(length = 1000)
    private String details;
    @Column(nullable = false)
    private Instant createdAt;

    protected AuditRecord() {}

    public AuditRecord(String action, String eventId, String accountId, String traceId, String outcome, String details) {
        this.action = action;
        this.eventId = eventId;
        this.accountId = accountId;
        this.traceId = traceId;
        this.outcome = outcome;
        this.details = details;
        this.createdAt = Instant.now();
    }

    public Long getId() { return id; }
    public String getAction() { return action; }
    public String getEventId() { return eventId; }
    public String getAccountId() { return accountId; }
    public String getTraceId() { return traceId; }
    public String getOutcome() { return outcome; }
    public String getDetails() { return details; }
    public Instant getCreatedAt() { return createdAt; }
}
