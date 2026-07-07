package com.example.account;

import com.example.account.dto.TransactionRequest;
import com.example.account.dto.TransactionResponse;
import com.example.account.service.AccountService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
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
        TransactionResponse duplicate = accountService.apply(credit);
        assertThat(accountService.getBalance("acct-a")).isEqualByComparingTo("75.00");
        assertThat(duplicate.balance()).isEqualByComparingTo("75.00");
    }

    @Test
    void unknownAccountBalanceDefaultsToZero() {
        assertThat(accountService.getBalance("acct-missing-balance")).isEqualByComparingTo(BigDecimal.ZERO);
    }

    @Test
    void accountDetailsReturnRecentTransactionsNewestFirst() {
        accountService.apply(new TransactionRequest("evt-account-details-older", "acct-details", "CREDIT", new BigDecimal("10.00"), "USD", Instant.parse("2026-05-15T10:00:00Z")));
        accountService.apply(new TransactionRequest("evt-account-details-newer", "acct-details", "DEBIT", new BigDecimal("3.00"), "USD", Instant.parse("2026-05-15T12:00:00Z")));

        var account = accountService.getAccount("acct-details");

        assertThat(account.accountId()).isEqualTo("acct-details");
        assertThat(account.balance()).isEqualByComparingTo("7.00");
        assertThat(account.recentTransactions())
                .extracting(TransactionResponse::eventId)
                .containsExactlyElementsOf(List.of("evt-account-details-newer", "evt-account-details-older"));
    }
}
