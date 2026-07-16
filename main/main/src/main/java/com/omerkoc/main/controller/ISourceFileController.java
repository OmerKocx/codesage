package com.omerkoc.main.controller;

import com.omerkoc.main.model.SourceFile;
import org.springframework.http.ResponseEntity;
import java.util.List;
import java.util.UUID;

public interface ISourceFileController {

    ResponseEntity<SourceFile> create(SourceFile sourceFile);

    ResponseEntity<SourceFile> getById(UUID id);

    ResponseEntity<List<SourceFile>> getAll();

    ResponseEntity<Void> delete(UUID id);

    ResponseEntity<List<SourceFile>> getByAnalysisId(UUID analysisId);

    ResponseEntity<List<SourceFile>> getByPackageName(String packageName);

}
