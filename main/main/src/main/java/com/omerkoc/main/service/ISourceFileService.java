package com.omerkoc.main.service;

import com.omerkoc.main.dto.SourceFileRequest;
import com.omerkoc.main.dto.SourceFileResponse;
import java.util.List;
import java.util.UUID;

public interface ISourceFileService {

    SourceFileResponse create(SourceFileRequest request);

    SourceFileResponse getById(UUID id);

    List<SourceFileResponse> getAll();

    void delete(UUID id);

    List<SourceFileResponse> getByAnalysisId(UUID analysisId);

    List<SourceFileResponse> getByPackageName(String packageName);

}
