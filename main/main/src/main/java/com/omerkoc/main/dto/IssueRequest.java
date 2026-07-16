package com.omerkoc.main.dto;

import lombok.Builder;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

@Builder
public record IssueRequest(
    @NotNull(message = "Analysis ID cannot be null")
    UUID analysisId,
    
    UUID sourceFileId,
    
    @NotBlank(message = "Severity cannot be blank")
    String severity,
    
    String category,
    Integer lineNumber,
    
    @NotBlank(message = "Title cannot be blank")
    String title,
    
    String description,
    String suggestion
) {}
