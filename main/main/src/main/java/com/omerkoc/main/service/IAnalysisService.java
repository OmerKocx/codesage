package com.omerkoc.main.service;

import com.omerkoc.main.dto.AnalysisRequest;
import com.omerkoc.main.dto.AnalysisResponse;
import java.util.List;
import java.util.UUID;

public interface IAnalysisService {

    AnalysisResponse create(AnalysisRequest request);

    AnalysisResponse getById(UUID id);

    List<AnalysisResponse> getAll();

    void delete(UUID id);

    List<AnalysisResponse> getByRepoUrl(String repoUrl);

    List<AnalysisResponse> getByStatus(String status);

}
