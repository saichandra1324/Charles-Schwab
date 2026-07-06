package com.example.account.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import javax.sql.DataSource;
import java.sql.Connection;
import java.util.Map;

@RestController
public class HealthController {
    private final DataSource dataSource;
    public HealthController(DataSource dataSource) { this.dataSource = dataSource; }
    @GetMapping("/health")
    public Map<String,Object> health() {
        try (Connection ignored = dataSource.getConnection()) { return Map.of("service", "account-service", "status", "UP", "database", "CONNECTED"); }
        catch (Exception e) { return Map.of("service", "account-service", "status", "DOWN", "database", "DISCONNECTED"); }
    }
}
