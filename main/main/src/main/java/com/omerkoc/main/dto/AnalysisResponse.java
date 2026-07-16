package com.omerkoc.main.dto;

import lombok.Builder;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Builder
public record AnalysisResponse(
    UUID id,
    String repoUrl,
    String status,
    Integer overallScore,
    Instant createdAt,
    List<SourceFileResponse> sourceFiles,
    List<IssueResponse> issues
) {}
