package com.omerkoc.main.mapper;

import com.omerkoc.main.dto.*;
import com.omerkoc.main.model.*;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class DtoMapper {

        public AnalysisResponse toResponse(Analysis analysis) {
                if (analysis == null)
                        return null;
                return AnalysisResponse.builder()
                                .id(analysis.getId())
                                .repoUrl(analysis.getRepoUrl())
                                .status(analysis.getStatus())
                                .overallScore(analysis.getOverallScore())
                                .createdAt(analysis.getCreatedAt())
                                .sourceFiles(analysis.getSourceFiles() != null ? analysis.getSourceFiles().stream()
                                                .map(this::toResponse).collect(Collectors.toList()) : null)
                                .issues(analysis.getIssues() != null ? analysis.getIssues().stream()
                                                .map(this::toResponse).collect(Collectors.toList()) : null)
                                .build();
        }

        public SourceFileResponse toResponse(SourceFile sourceFile) {
                if (sourceFile == null)
                        return null;
                return SourceFileResponse.builder()
                                .id(sourceFile.getId())
                                .analysisId(sourceFile.getAnalysis() != null ? sourceFile.getAnalysis().getId() : null)
                                .filePath(sourceFile.getFilePath())
                                .packageName(sourceFile.getPackageName())
                                .className(sourceFile.getClassName())
                                .build();
        }

        public IssueResponse toResponse(Issue issue) {
                if (issue == null)
                        return null;
                return IssueResponse.builder()
                                .id(issue.getId())
                                .analysisId(issue.getAnalysis() != null ? issue.getAnalysis().getId() : null)
                                .sourceFileId(issue.getSourceFile() != null ? issue.getSourceFile().getId() : null)
                                .severity(issue.getSeverity())
                                .category(issue.getCategory())
                                .lineNumber(issue.getLineNumber())
                                .title(issue.getTitle())
                                .description(issue.getDescription())
                                .suggestion(issue.getSuggestion())
                                .build();
        }

        public Analysis toEntity(AnalysisRequest request) {
                if (request == null)
                        return null;
                return Analysis.builder()
                                .repoUrl(request.repoUrl())
                                .status(request.status())
                                .overallScore(request.overallScore())
                                .build();
        }
}
