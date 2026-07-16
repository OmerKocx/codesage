package com.omerkoc.main.service;

import com.omerkoc.main.model.SourceFile;
import java.util.List;
import java.util.UUID;

public interface ISourceFileService {

    SourceFile create(SourceFile sourceFile);

    SourceFile getById(UUID id);

    List<SourceFile> getAll();

    void delete(UUID id);

    List<SourceFile> getByAnalysisId(UUID analysisId);

    List<SourceFile> getByPackageName(String packageName);

}
