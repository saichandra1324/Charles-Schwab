package com.example.gateway.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Map;

public record EventRequest(
        @NotBlank String eventId,
        @NotBlank String accountId,
        @Pattern(regexp = "CREDIT|DEBIT", message = "must be CREDIT or DEBIT") String type,
        @NotNull @DecimalMin(value = "0.00", inclusive = false, message = "must be greater than 0") BigDecimal amount,
        @NotBlank String currency,
        @NotNull Instant eventTimestamp,
        Map<String, Object> metadata
) {}
