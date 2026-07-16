package com.omerkoc.main.dto;

import lombok.Builder;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

@Builder
public record SourceFileRequest(
    @NotNull(message = "Analysis ID cannot be null")
    UUID analysisId,
    
    @NotBlank(message = "File path cannot be blank")
    String filePath,
    
    String packageName,
    String className
) {}
