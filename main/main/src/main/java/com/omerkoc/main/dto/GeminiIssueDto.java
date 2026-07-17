package com.omerkoc.main.dto;

public record GeminiIssueDto(
        String filePath,
        Integer lineNumber,
        String severity, // HIGH, MEDIUM, LOW
        String category, // SECURITY, BUG, PERFORMANCE, STYLE
        String title,
        String description,
        String suggestion) {
}
