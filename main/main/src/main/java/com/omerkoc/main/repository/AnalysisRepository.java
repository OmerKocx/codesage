package com.omerkoc.main.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.omerkoc.main.model.Analysis;

@Repository
public interface AnalysisRepository extends JpaRepository<Analysis, UUID> {
    List<Analysis> findByRepoUrl(String repoUrl);
    List<Analysis> findByStatus(String status);
}