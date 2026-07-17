package com.omerkoc.main.service;

import com.omerkoc.main.dto.GeminiAnalysisResult;

public interface IGeminiService {
    GeminiAnalysisResult analyzeCodebase(String mergedCodeText);
}
