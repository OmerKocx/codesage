package com.omerkoc.main.dto;

import lombok.Builder;
import java.util.UUID;

@Builder
public record SourceFileResponse(
    UUID id,
    UUID analysisId,
    String filePath,
    String packageName,
    String className
) {}
