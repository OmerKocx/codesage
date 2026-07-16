package com.omerkoc.main.controller;

import jakarta.validation.Valid;
import com.omerkoc.main.dto.IssueRequest;
import com.omerkoc.main.dto.IssueResponse;
import org.springframework.http.ResponseEntity;
import java.util.List;
import java.util.UUID;

public interface IIssueController {

    ResponseEntity<IssueResponse> create(@Valid IssueRequest request);

    ResponseEntity<IssueResponse> getById(UUID id);

    ResponseEntity<List<IssueResponse>> getAll();

    ResponseEntity<Void> delete(UUID id);

    ResponseEntity<List<IssueResponse>> getByAnalysisId(UUID analysisId);

    ResponseEntity<List<IssueResponse>> getBySourceFileId(UUID sourceFileId);

    ResponseEntity<List<IssueResponse>> getBySeverity(String severity);

    ResponseEntity<List<IssueResponse>> getByCategory(String category);

}
