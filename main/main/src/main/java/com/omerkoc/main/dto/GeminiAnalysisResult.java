package com.omerkoc.main.dto;

import java.util.List;

public record GeminiAnalysisResult(
                Integer overallScore,
                List<GeminiIssueDto> issues) {

}
