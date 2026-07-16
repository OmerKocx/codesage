package com.omerkoc.main.controller;

import com.omerkoc.main.model.Analysis;
import org.springframework.http.ResponseEntity;
import java.util.List;
import java.util.UUID;

public interface IAnalysisController {

    ResponseEntity<Analysis> create(Analysis analysis);

    ResponseEntity<Analysis> getById(UUID id);

    ResponseEntity<List<Analysis>> getAll();

    ResponseEntity<Void> delete(UUID id);

    ResponseEntity<List<Analysis>> getByRepoUrl(String repoUrl);

    ResponseEntity<List<Analysis>> getByStatus(String status);

}
