package com.omerkoc.main.controller;

import com.omerkoc.main.model.Issue;
import org.springframework.http.ResponseEntity;
import java.util.List;
import java.util.UUID;

public interface IIssueController {

    ResponseEntity<Issue> create(Issue issue);

    ResponseEntity<Issue> getById(UUID id);

    ResponseEntity<List<Issue>> getAll();

    ResponseEntity<Void> delete(UUID id);

    ResponseEntity<List<Issue>> getByAnalysisId(UUID analysisId);

    ResponseEntity<List<Issue>> getBySourceFileId(UUID sourceFileId);

    ResponseEntity<List<Issue>> getBySeverity(String severity);

    ResponseEntity<List<Issue>> getByCategory(String category);

}
