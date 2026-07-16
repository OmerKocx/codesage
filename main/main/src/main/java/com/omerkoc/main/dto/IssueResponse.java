package com.omerkoc.main.dto;

import lombok.Builder;
import java.util.UUID;

@Builder
public record IssueResponse(
    UUID id,
    UUID analysisId,
    UUID sourceFileId,
    String severity,
    String category,
    Integer lineNumber,
    String title,
    String description,
    String suggestion
) {}
