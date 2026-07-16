package com.omerkoc.main.service.impl;

import org.springframework.stereotype.Service;

import com.omerkoc.main.exceptions.NotFoundException;
import com.omerkoc.main.model.Analysis;
import com.omerkoc.main.repository.AnalysisRepository;
import com.omerkoc.main.service.IAnalysisService;

import lombok.RequiredArgsConstructor;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AnalysisServiceImpl implements IAnalysisService {

    private final AnalysisRepository analysisRepository;

    @Override
    public Analysis create(Analysis analysis) {
        if (analysis.getCreatedAt() == null) {
            analysis.setCreatedAt(Instant.now());
        }
        return analysisRepository.save(analysis);
    }

    @Override
    public Analysis getById(UUID id) {
        return analysisRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Analysis not found with id: " + id));
    }

    @Override
    public List<Analysis> getAll() {
        return analysisRepository.findAll();
    }

    @Override
    public void delete(UUID id) {
        Analysis analysis = getById(id);
        analysisRepository.delete(analysis);
    }

    @Override
    public List<Analysis> getByRepoUrl(String repoUrl) {
        return analysisRepository.findByRepoUrl(repoUrl);
    }

    @Override
    public List<Analysis> getByStatus(String status) {
        return analysisRepository.findByStatus(status);
    }

}
