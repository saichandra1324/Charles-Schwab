package com.example.account.service;

import com.example.account.dto.*;
import com.example.account.entity.*;
import com.example.account.repository.*;
import io.micrometer.core.instrument.MeterRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class AccountService {
    private static final Logger log = LoggerFactory.getLogger(AccountService.class);
    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    private final MeterRegistry meterRegistry;
    private final AuditService auditService;

    public AccountService(AccountRepository accountRepository, TransactionRepository transactionRepository, MeterRegistry meterRegistry, AuditService auditService) {
        this.accountRepository = accountRepository; this.transactionRepository = transactionRepository; this.meterRegistry = meterRegistry; this.auditService = auditService;
    }

    @Transactional
    public TransactionResponse apply(TransactionRequest request) {
        var existing = transactionRepository.findByEventId(request.eventId());
        if (existing.isPresent()) {
            log.info("Duplicate transaction ignored eventId={}", request.eventId());
            auditService.record("TRANSACTION_DUPLICATE", request.eventId(), request.accountId(), "IGNORED", "Duplicate eventId detected in account service");
            meterRegistry.counter("account.transactions.duplicate").increment();
            var acc = accountRepository.findById(request.accountId()).orElse(new Account(request.accountId()));
            return toResponse(existing.get(), acc.getBalance());
        }
        Account account = accountRepository.findById(request.accountId()).orElseGet(() -> new Account(request.accountId()));
        BigDecimal newBalance = "CREDIT".equals(request.type()) ? account.getBalance().add(request.amount()) : account.getBalance().subtract(request.amount());
        account.setBalance(newBalance);
        accountRepository.save(account);
        TransactionRecord tx = transactionRepository.save(new TransactionRecord(request.eventId(), request.accountId(), request.type(), request.amount(), request.currency(), request.eventTimestamp()));
        meterRegistry.counter("account.transactions.applied", "type", request.type()).increment();
        log.info("Transaction applied eventId={} accountId={} balance={}", request.eventId(), request.accountId(), newBalance);
        auditService.record("TRANSACTION_APPLIED", request.eventId(), request.accountId(), "SUCCESS", "Balance updated to " + newBalance);
        return toResponse(tx, newBalance);
    }

    @Transactional(readOnly = true)
    public AccountResponse getAccount(String accountId) {
        Account account = accountRepository.findById(accountId).orElseGet(() -> new Account(accountId));
        List<TransactionResponse> txs = transactionRepository.findTop10ByAccountIdOrderByEventTimestampDesc(accountId).stream()
                .map(tx -> toResponse(tx, account.getBalance())).toList();
        return new AccountResponse(accountId, account.getBalance(), txs);
    }

    @Transactional(readOnly = true)
    public BigDecimal getBalance(String accountId) {
        BigDecimal balance = accountRepository.findById(accountId).map(Account::getBalance).orElse(BigDecimal.ZERO);
        auditService.record("BALANCE_READ", null, accountId, "SUCCESS", "Balance query returned " + balance);
        return balance;
    }

    private TransactionResponse toResponse(TransactionRecord tx, BigDecimal balance) {
        return new TransactionResponse(tx.getEventId(), tx.getAccountId(), tx.getType(), tx.getAmount(), tx.getCurrency(), tx.getEventTimestamp(), balance);
    }
}
