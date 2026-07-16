package com.omerkoc.main.controller;

import jakarta.validation.Valid;
import com.omerkoc.main.dto.AnalysisRequest;
import com.omerkoc.main.dto.AnalysisResponse;
import org.springframework.http.ResponseEntity;
import java.util.List;
import java.util.UUID;

public interface IAnalysisController {

    ResponseEntity<AnalysisResponse> create(@Valid AnalysisRequest request);

    ResponseEntity<AnalysisResponse> getById(UUID id);

    ResponseEntity<List<AnalysisResponse>> getAll();

    ResponseEntity<Void> delete(UUID id);

    ResponseEntity<List<AnalysisResponse>> getByRepoUrl(String repoUrl);

    ResponseEntity<List<AnalysisResponse>> getByStatus(String status);

}
