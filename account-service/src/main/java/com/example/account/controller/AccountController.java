package com.example.account.controller;

import com.example.account.dto.*;
import com.example.account.service.AccountService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/accounts")
public class AccountController {
    private final AccountService accountService;
    public AccountController(AccountService accountService) { this.accountService = accountService; }

    @PostMapping("/{accountId}/transactions")
    public ResponseEntity<TransactionResponse> apply(@PathVariable String accountId, @Valid @RequestBody TransactionRequest request) {
        if (!accountId.equals(request.accountId())) throw new IllegalArgumentException("Path accountId must match request accountId");
        return ResponseEntity.ok(accountService.apply(request));
    }

    @GetMapping("/{accountId}/balance")
    public Map<String, Object> balance(@PathVariable String accountId) { return Map.of("accountId", accountId, "balance", accountService.getBalance(accountId)); }

    @GetMapping("/{accountId}")
    public AccountResponse account(@PathVariable String accountId) { return accountService.getAccount(accountId); }

}
