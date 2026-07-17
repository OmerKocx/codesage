package com.omerkoc.main.service.impl;

import org.springframework.stereotype.Service;

import com.omerkoc.main.exceptions.NotFoundException;
import com.omerkoc.main.exceptions.BadRequestException;
import com.omerkoc.main.dto.AnalysisRequest;
import com.omerkoc.main.dto.AnalysisResponse;
import com.omerkoc.main.dto.GeminiAnalysisResult;
import com.omerkoc.main.dto.GeminiIssueDto;
import com.omerkoc.main.mapper.DtoMapper;
import com.omerkoc.main.model.Analysis;
import com.omerkoc.main.model.SourceFile;
import com.omerkoc.main.model.Issue;
import com.omerkoc.main.repository.AnalysisRepository;
import com.omerkoc.main.service.IAnalysisService;
import com.omerkoc.main.service.IGitService;
import com.omerkoc.main.service.IGeminiService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import java.time.Instant;
import java.nio.file.Path;
import java.util.List;
import java.util.UUID;
import java.util.Map;
import java.util.HashMap;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AnalysisServiceImpl implements IAnalysisService {

    private final AnalysisRepository analysisRepository;
    private final DtoMapper dtoMapper;
    private final IGitService gitService;
    private final IGeminiService geminiService;

    @Override
    public AnalysisResponse create(AnalysisRequest request) {
        Analysis analysis = dtoMapper.toEntity(request);
        if (analysis.getCreatedAt() == null) {
            analysis.setCreatedAt(Instant.now());
        }
        analysis.setStatus("PROCESSING");

        Path tempDir = null;
        try {
            log.info("Starting analysis workflow for repository: {}", analysis.getRepoUrl());

            // 1. Clone repository to a unique temp directory
            tempDir = gitService.cloneRepository(analysis.getRepoUrl());

            // 2. Scan code files and merge them into a single text representation
            String mergedCodeText = gitService.mergeFilesToText(tempDir);

            // 3. Send codebase text to Gemini API for static analysis
            GeminiAnalysisResult analysisResult = geminiService.analyzeCodebase(mergedCodeText);

            // 4. Update analysis metadata
            analysis.setOverallScore(analysisResult.overallScore());
            analysis.setStatus("COMPLETED");

            // 5. Map DTO issues to DB Entities (SourceFile and Issue)
            Map<String, SourceFile> sourceFileMap = new HashMap<>();
            for (GeminiIssueDto issueDto : analysisResult.issues()) {
                SourceFile sourceFile = sourceFileMap.computeIfAbsent(issueDto.filePath(), filePath -> {
                    SourceFile sf = SourceFile.builder()
                            .analysis(analysis)
                            .filePath(filePath)
                            .className(extractClassName(filePath))
                            .build();
                    analysis.getSourceFiles().add(sf);
                    return sf;
                });

                Issue issue = Issue.builder()
                        .analysis(analysis)
                        .sourceFile(sourceFile)
                        .severity(issueDto.severity())
                        .category(issueDto.category())
                        .lineNumber(issueDto.lineNumber())
                        .title(issueDto.title())
                        .description(issueDto.description())
                        .suggestion(issueDto.suggestion())
                        .build();

                analysis.getIssues().add(issue);
                sourceFile.getIssues().add(issue);
            }

            log.info("Repository analysis completed successfully. Overall Score: {}", analysis.getOverallScore());

        } catch (Exception e) {
            analysis.setStatus("FAILED");
            log.error("Failed to complete repository analysis for {}: {}", analysis.getRepoUrl(), e.getMessage(), e);
            analysisRepository.save(analysis);
            throw new BadRequestException("Analysis workflow failed: " + e.getMessage());
        } finally {
            // 6. Clean up temporary cloned files
            if (tempDir != null) {
                gitService.cleanUp(tempDir);
            }
        }

        // Save populated analysis and cascade save all source files/issues
        Analysis savedAnalysis = analysisRepository.save(analysis);
        return dtoMapper.toResponse(savedAnalysis);
    }

    private String extractClassName(String filePath) {
        if (filePath == null)
            return null;
        String cleanPath = filePath.replace("\\", "/");
        String fileName = cleanPath.substring(cleanPath.lastIndexOf("/") + 1);
        if (fileName.contains(".")) {
            return fileName.substring(0, fileName.lastIndexOf("."));
        }
        return fileName;
    }

    @Override
    public AnalysisResponse getById(UUID id) {
        Analysis analysis = analysisRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Analysis not found with id: " + id));
        return dtoMapper.toResponse(analysis);
    }

    @Override
    public List<AnalysisResponse> getAll() {
        return analysisRepository.findAll().stream()
                .map(dtoMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public void delete(UUID id) {
        Analysis analysis = analysisRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Analysis not found with id: " + id));
        analysisRepository.delete(analysis);
    }

    @Override
    public List<AnalysisResponse> getByRepoUrl(String repoUrl) {
        return analysisRepository.findByRepoUrl(repoUrl).stream()
                .map(dtoMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<AnalysisResponse> getByStatus(String status) {
        return analysisRepository.findByStatus(status).stream()
                .map(dtoMapper::toResponse)
                .collect(Collectors.toList());
    }

}
