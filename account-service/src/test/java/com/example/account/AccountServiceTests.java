package com.example.account;

import com.example.account.dto.TransactionRequest;
import com.example.account.service.AccountService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import java.math.BigDecimal;
import java.time.Instant;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class AccountServiceTests {
    @Autowired AccountService accountService;

    @Test
    void appliesCreditsAndDebitsAndPreventsDuplicateTransaction() {
        var credit = new TransactionRequest("evt-a1", "acct-a", "CREDIT", new BigDecimal("100.00"), "USD", Instant.parse("2026-05-15T10:00:00Z"));
        var debit = new TransactionRequest("evt-a2", "acct-a", "DEBIT", new BigDecimal("25.00"), "USD", Instant.parse("2026-05-15T11:00:00Z"));
        accountService.apply(credit);
        accountService.apply(debit);
        accountService.apply(credit);
        assertThat(accountService.getBalance("acct-a")).isEqualByComparingTo("75.00");
    }
}
