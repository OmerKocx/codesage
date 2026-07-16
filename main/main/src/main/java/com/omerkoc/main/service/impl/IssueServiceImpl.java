package com.omerkoc.main.service.impl;

import org.springframework.stereotype.Service;

import com.omerkoc.main.exceptions.NotFoundException;
import com.omerkoc.main.dto.IssueRequest;
import com.omerkoc.main.dto.IssueResponse;
import com.omerkoc.main.mapper.DtoMapper;
import com.omerkoc.main.model.Analysis;
import com.omerkoc.main.model.Issue;
import com.omerkoc.main.model.SourceFile;
import com.omerkoc.main.repository.AnalysisRepository;
import com.omerkoc.main.repository.IssueRepository;
import com.omerkoc.main.repository.SourceFileRepository;
import com.omerkoc.main.service.IIssueService;

import lombok.RequiredArgsConstructor;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class IssueServiceImpl implements IIssueService {

    private final IssueRepository issueRepository;
    private final AnalysisRepository analysisRepository;
    private final SourceFileRepository sourceFileRepository;
    private final DtoMapper dtoMapper;

    @Override
    public IssueResponse create(IssueRequest request) {
        Analysis analysis = analysisRepository.findById(request.analysisId())
                .orElseThrow(() -> new NotFoundException("Analysis not found with id: " + request.analysisId()));
                
        SourceFile sourceFile = null;
        if (request.sourceFileId() != null) {
            sourceFile = sourceFileRepository.findById(request.sourceFileId())
                .orElseThrow(() -> new NotFoundException("Source file not found with id: " + request.sourceFileId()));
        }

        Issue issue = Issue.builder()
                .analysis(analysis)
                .sourceFile(sourceFile)
                .severity(request.severity())
                .category(request.category())
                .lineNumber(request.lineNumber())
                .title(request.title())
                .description(request.description())
                .suggestion(request.suggestion())
                .build();

        return dtoMapper.toResponse(issueRepository.save(issue));
    }

    @Override
    public IssueResponse getById(UUID id) {
        Issue issue = issueRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Issue not found with id: " + id));
        return dtoMapper.toResponse(issue);
    }

    @Override
    public List<IssueResponse> getAll() {
        return issueRepository.findAll().stream()
                .map(dtoMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public void delete(UUID id) {
        Issue issue = issueRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Issue not found with id: " + id));
        issueRepository.delete(issue);
    }

    @Override
    public List<IssueResponse> getByAnalysisId(UUID analysisId) {
        return issueRepository.findByAnalysisId(analysisId).stream()
                .map(dtoMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<IssueResponse> getBySourceFileId(UUID sourceFileId) {
        return issueRepository.findBySourceFileId(sourceFileId).stream()
                .map(dtoMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<IssueResponse> getBySeverity(String severity) {
        return issueRepository.findBySeverity(severity).stream()
                .map(dtoMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<IssueResponse> getByCategory(String category) {
        return issueRepository.findByCategory(category).stream()
                .map(dtoMapper::toResponse)
                .collect(Collectors.toList());
    }

}
