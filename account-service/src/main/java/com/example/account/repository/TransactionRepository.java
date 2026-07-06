package com.example.account.repository;

import com.example.account.entity.TransactionRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface TransactionRepository extends JpaRepository<TransactionRecord, Long> {
    Optional<TransactionRecord> findByEventId(String eventId);
    List<TransactionRecord> findTop10ByAccountIdOrderByEventTimestampDesc(String accountId);
}
