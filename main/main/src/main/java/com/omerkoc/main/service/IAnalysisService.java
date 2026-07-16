package com.omerkoc.main.service;

import com.omerkoc.main.model.Analysis;
import java.util.List;
import java.util.UUID;

public interface IAnalysisService {

    Analysis create(Analysis analysis);

    Analysis getById(UUID id);

    List<Analysis> getAll();

    void delete(UUID id);

    List<Analysis> getByRepoUrl(String repoUrl);

    List<Analysis> getByStatus(String status);

}
