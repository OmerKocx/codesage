package com.omerkoc.main.controller;

import jakarta.validation.Valid;
import com.omerkoc.main.dto.SourceFileRequest;
import com.omerkoc.main.dto.SourceFileResponse;
import org.springframework.http.ResponseEntity;
import java.util.List;
import java.util.UUID;

public interface ISourceFileController {

    ResponseEntity<SourceFileResponse> create(@Valid SourceFileRequest request);

    ResponseEntity<SourceFileResponse> getById(UUID id);

    ResponseEntity<List<SourceFileResponse>> getAll();

    ResponseEntity<Void> delete(UUID id);

    ResponseEntity<List<SourceFileResponse>> getByAnalysisId(UUID analysisId);

    ResponseEntity<List<SourceFileResponse>> getByPackageName(String packageName);

}
