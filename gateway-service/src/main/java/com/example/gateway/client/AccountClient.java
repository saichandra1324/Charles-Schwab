package com.example.gateway.client;

import com.example.gateway.dto.*;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@FeignClient(name = "accountClient", url = "${account-service.url}")
public interface AccountClient {
    @PostMapping("/accounts/{accountId}/transactions")
    TransactionResponse apply(@PathVariable String accountId, @RequestBody TransactionRequest request);

    @GetMapping("/accounts/{accountId}/balance")
    Map<String,Object> balance(@PathVariable String accountId);

    @GetMapping("/accounts/{accountId}")
    AccountResponse account(@PathVariable String accountId);
}
