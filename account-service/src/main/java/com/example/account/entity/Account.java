package com.example.account.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import java.math.BigDecimal;

@Entity
public class Account {
    @Id
    private String accountId;
    private BigDecimal balance = BigDecimal.ZERO;

    protected Account() {}
    public Account(String accountId) { this.accountId = accountId; }
    public String getAccountId() { return accountId; }
    public BigDecimal getBalance() { return balance; }
    public void setBalance(BigDecimal balance) { this.balance = balance; }
}
