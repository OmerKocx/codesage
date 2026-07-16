package com.omerkoc.main.service;

import com.omerkoc.main.dto.IssueRequest;
import com.omerkoc.main.dto.IssueResponse;
import java.util.List;
import java.util.UUID;

public interface IIssueService {

    IssueResponse create(IssueRequest request);

    IssueResponse getById(UUID id);

    List<IssueResponse> getAll();

    void delete(UUID id);

    List<IssueResponse> getByAnalysisId(UUID analysisId);

    List<IssueResponse> getBySourceFileId(UUID sourceFileId);

    List<IssueResponse> getBySeverity(String severity);

    List<IssueResponse> getByCategory(String category);

}
