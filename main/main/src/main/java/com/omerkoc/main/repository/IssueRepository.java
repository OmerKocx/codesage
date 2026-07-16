package com.omerkoc.main.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.omerkoc.main.model.Issue;

@Repository
public interface IssueRepository extends JpaRepository<Issue, UUID> {
    List<Issue> findByAnalysisId(UUID analysisId);
    List<Issue> findBySourceFileId(UUID sourceFileId);
    List<Issue> findBySeverity(String severity);
    List<Issue> findByCategory(String category);
}
