package com.example.gateway.repository;

import com.example.gateway.entity.AuditRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AuditRepository extends JpaRepository<AuditRecord, Long> {
    List<AuditRecord> findTop50ByOrderByCreatedAtDesc();
    List<AuditRecord> findByEventIdOrderByCreatedAtAsc(String eventId);
}
