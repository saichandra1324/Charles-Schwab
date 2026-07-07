package com.example.account.controller;

import com.example.account.dto.*;
import com.example.account.service.AccountService;
import com.example.account.service.AuditService;
import com.example.account.entity.AuditRecord;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
import java.util.List;

@RestController
@RequestMapping("/accounts")
public class AccountController {
    private final AccountService accountService;
    private final AuditService auditService;
    public AccountController(AccountService accountService, AuditService auditService) { this.accountService = accountService; this.auditService = auditService; }

    @PostMapping("/{accountId}/transactions")
    public ResponseEntity<TransactionResponse> apply(
            @PathVariable("accountId") String accountId,
            @Valid @RequestBody TransactionRequest request) {
        if (!accountId.equals(request.accountId())) throw new IllegalArgumentException("Path accountId must match request accountId");
        return ResponseEntity.ok(accountService.apply(request));
    }

    @GetMapping("/{accountId}/balance")
    public Map<String, Object> balance(@PathVariable("accountId") String accountId) { return Map.of("accountId", accountId, "balance", accountService.getBalance(accountId)); }

    @GetMapping("/{accountId}")
    public AccountResponse account(@PathVariable("accountId") String accountId) { return accountService.getAccount(accountId); }


    @GetMapping("/audit")
    public List<AuditRecord> latestAudit() { return auditService.latest(); }

    @GetMapping("/audit/events/{eventId}")
    public List<AuditRecord> eventAudit(@PathVariable("eventId") String eventId) { return auditService.byEvent(eventId); }

}
