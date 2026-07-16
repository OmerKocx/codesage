package com.omerkoc.main.service.impl;

import org.springframework.stereotype.Service;

import com.omerkoc.main.exceptions.NotFoundException;
import com.omerkoc.main.model.Issue;
import com.omerkoc.main.repository.IssueRepository;
import com.omerkoc.main.service.IIssueService;

import lombok.RequiredArgsConstructor;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class IssueServiceImpl implements IIssueService {

    private final IssueRepository issueRepository;

    @Override
    public Issue create(Issue issue) {
        return issueRepository.save(issue);
    }

    @Override
    public Issue getById(UUID id) {
        return issueRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Issue not found with id: " + id));
    }

    @Override
    public List<Issue> getAll() {
        return issueRepository.findAll();
    }

    @Override
    public void delete(UUID id) {
        Issue issue = getById(id);
        issueRepository.delete(issue);
    }

    @Override
    public List<Issue> getByAnalysisId(UUID analysisId) {
        return issueRepository.findByAnalysisId(analysisId);
    }

    @Override
    public List<Issue> getBySourceFileId(UUID sourceFileId) {
        return issueRepository.findBySourceFileId(sourceFileId);
    }

    @Override
    public List<Issue> getBySeverity(String severity) {
        return issueRepository.findBySeverity(severity);
    }

    @Override
    public List<Issue> getByCategory(String category) {
        return issueRepository.findByCategory(category);
    }

}
