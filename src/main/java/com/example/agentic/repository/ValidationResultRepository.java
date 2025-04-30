package com.example.agentic.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.agentic.domain.ValidationResult;

@Repository
public interface ValidationResultRepository extends JpaRepository<ValidationResult, Long> {}
