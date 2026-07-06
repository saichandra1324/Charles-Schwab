package com.example.gateway.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "events", indexes = @Index(name = "idx_event_account_ts", columnList = "accountId,eventTimestamp"))
public class EventRecord {
    @Id
    private String eventId;
    @Column(nullable = false)
    private String accountId;
    @Column(nullable = false)
    private String type;
    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;
    @Column(nullable = false)
    private String currency;
    @Column(nullable = false)
    private Instant eventTimestamp;
    @Column(length = 4000)
    private String metadataJson;
    @Column(nullable = false)
    private String status;
    @Column(nullable = false)
    private Instant createdAt;

    protected EventRecord() {}
    public EventRecord(String eventId, String accountId, String type, BigDecimal amount, String currency, Instant eventTimestamp, String metadataJson, String status) {
        this.eventId = eventId; this.accountId = accountId; this.type = type; this.amount = amount; this.currency = currency; this.eventTimestamp = eventTimestamp; this.metadataJson = metadataJson; this.status = status; this.createdAt = Instant.now();
    }
    public String getEventId() { return eventId; }
    public String getAccountId() { return accountId; }
    public String getType() { return type; }
    public BigDecimal getAmount() { return amount; }
    public String getCurrency() { return currency; }
    public Instant getEventTimestamp() { return eventTimestamp; }
    public String getMetadataJson() { return metadataJson; }
    public String getStatus() { return status; }
    public Instant getCreatedAt() { return createdAt; }
    public void setStatus(String status) { this.status = status; }
}
