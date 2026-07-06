package com.example.account.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.Instant;

public record TransactionRequest(
        @NotBlank String eventId,
        @NotBlank String accountId,
        @Pattern(regexp = "CREDIT|DEBIT") String type,
        @NotNull @DecimalMin(value = "0.00", inclusive = false) BigDecimal amount,
        @NotBlank String currency,
        @NotNull Instant eventTimestamp
) {}
