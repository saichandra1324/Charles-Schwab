package com.example.account.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "transactions", indexes = @Index(name = "idx_tx_account_ts", columnList = "accountId,eventTimestamp"))
public class TransactionRecord {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, unique = true)
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

    protected TransactionRecord() {}
    public TransactionRecord(String eventId, String accountId, String type, BigDecimal amount, String currency, Instant eventTimestamp) {
        this.eventId = eventId; this.accountId = accountId; this.type = type; this.amount = amount; this.currency = currency; this.eventTimestamp = eventTimestamp;
    }
    public Long getId() { return id; }
    public String getEventId() { return eventId; }
    public String getAccountId() { return accountId; }
    public String getType() { return type; }
    public BigDecimal getAmount() { return amount; }
    public String getCurrency() { return currency; }
    public Instant getEventTimestamp() { return eventTimestamp; }
}
