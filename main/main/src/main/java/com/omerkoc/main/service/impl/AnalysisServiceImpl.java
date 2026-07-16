package com.omerkoc.main.service.impl;

import org.springframework.stereotype.Service;

import com.omerkoc.main.exceptions.NotFoundException;
import com.omerkoc.main.dto.AnalysisRequest;
import com.omerkoc.main.dto.AnalysisResponse;
import com.omerkoc.main.mapper.DtoMapper;
import com.omerkoc.main.model.Analysis;
import com.omerkoc.main.repository.AnalysisRepository;
import com.omerkoc.main.service.IAnalysisService;

import lombok.RequiredArgsConstructor;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AnalysisServiceImpl implements IAnalysisService {

    private final AnalysisRepository analysisRepository;
    private final DtoMapper dtoMapper;

    @Override
    public AnalysisResponse create(AnalysisRequest request) {
        Analysis analysis = dtoMapper.toEntity(request);
        if (analysis.getCreatedAt() == null) {
            analysis.setCreatedAt(Instant.now());
        }
        return dtoMapper.toResponse(analysisRepository.save(analysis));
    }

    @Override
    public AnalysisResponse getById(UUID id) {
        Analysis analysis = analysisRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Analysis not found with id: " + id));
        return dtoMapper.toResponse(analysis);
    }

    @Override
    public List<AnalysisResponse> getAll() {
        return analysisRepository.findAll().stream()
                .map(dtoMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public void delete(UUID id) {
        Analysis analysis = analysisRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Analysis not found with id: " + id));
        analysisRepository.delete(analysis);
    }

    @Override
    public List<AnalysisResponse> getByRepoUrl(String repoUrl) {
        return analysisRepository.findByRepoUrl(repoUrl).stream()
                .map(dtoMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<AnalysisResponse> getByStatus(String status) {
        return analysisRepository.findByStatus(status).stream()
                .map(dtoMapper::toResponse)
                .collect(Collectors.toList());
    }

}
