package com.omerkoc.main.dto;

import lombok.Builder;
import jakarta.validation.constraints.NotBlank;

@Builder
public record AnalysisRequest(
    @NotBlank(message = "Repository URL cannot be blank")
    String repoUrl,
    
    @NotBlank(message = "Status cannot be blank")
    String status,
    
    Integer overallScore
) {}
