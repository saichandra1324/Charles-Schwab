package com.example.gateway.service;

import com.example.gateway.entity.AuditRecord;
import com.example.gateway.repository.AuditRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AuditService {
    private static final Logger log = LoggerFactory.getLogger(AuditService.class);
    private final AuditRepository auditRepository;

    public AuditService(AuditRepository auditRepository) {
        this.auditRepository = auditRepository;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void record(String action, String eventId, String accountId, String outcome, String details) {
        String traceId = MDC.get("traceId");
        auditRepository.save(new AuditRecord(action, eventId, accountId, traceId, outcome, details));
        log.info("audit action={} outcome={} eventId={} accountId={}", action, outcome, eventId, accountId);
    }

    @Transactional(readOnly = true)
    public List<AuditRecord> latest() {
        return auditRepository.findTop50ByOrderByCreatedAtDesc();
    }

    @Transactional(readOnly = true)
    public List<AuditRecord> byEvent(String eventId) {
        return auditRepository.findByEventIdOrderByCreatedAtAsc(eventId);
    }
}
