package com.example.agentic.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.agentic.domain.StatementData;

@Repository
public interface StatementRepository extends JpaRepository<StatementData, Long> {
}
