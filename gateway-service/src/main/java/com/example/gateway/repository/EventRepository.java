package com.example.gateway.repository;

import com.example.gateway.entity.EventRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface EventRepository extends JpaRepository<EventRecord, String> {
    List<EventRecord> findByAccountIdOrderByEventTimestampAsc(String accountId);
}
