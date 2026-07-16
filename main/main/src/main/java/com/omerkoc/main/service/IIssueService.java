package com.omerkoc.main.service;

import com.omerkoc.main.model.Issue;
import java.util.List;
import java.util.UUID;

public interface IIssueService {

    Issue create(Issue issue);

    Issue getById(UUID id);

    List<Issue> getAll();

    void delete(UUID id);

    List<Issue> getByAnalysisId(UUID analysisId);

    List<Issue> getBySourceFileId(UUID sourceFileId);

    List<Issue> getBySeverity(String severity);

    List<Issue> getByCategory(String category);

}
