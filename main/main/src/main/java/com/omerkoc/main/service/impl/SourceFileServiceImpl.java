package com.omerkoc.main.service.impl;

import org.springframework.stereotype.Service;

import com.omerkoc.main.exceptions.NotFoundException;
import com.omerkoc.main.dto.SourceFileRequest;
import com.omerkoc.main.dto.SourceFileResponse;
import com.omerkoc.main.mapper.DtoMapper;
import com.omerkoc.main.model.Analysis;
import com.omerkoc.main.model.SourceFile;
import com.omerkoc.main.repository.AnalysisRepository;
import com.omerkoc.main.repository.SourceFileRepository;
import com.omerkoc.main.service.ISourceFileService;

import lombok.RequiredArgsConstructor;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SourceFileServiceImpl implements ISourceFileService {

    private final SourceFileRepository sourceFileRepository;
    private final AnalysisRepository analysisRepository;
    private final DtoMapper dtoMapper;

    @Override
    public SourceFileResponse create(SourceFileRequest request) {
        Analysis analysis = analysisRepository.findById(request.analysisId())
                .orElseThrow(() -> new NotFoundException("Analysis not found with id: " + request.analysisId()));
                
        SourceFile sourceFile = SourceFile.builder()
                .analysis(analysis)
                .filePath(request.filePath())
                .packageName(request.packageName())
                .className(request.className())
                .build();
                
        return dtoMapper.toResponse(sourceFileRepository.save(sourceFile));
    }

    @Override
    public SourceFileResponse getById(UUID id) {
        SourceFile sourceFile = sourceFileRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Source file not found with id: " + id));
        return dtoMapper.toResponse(sourceFile);
    }

    @Override
    public List<SourceFileResponse> getAll() {
        return sourceFileRepository.findAll().stream()
                .map(dtoMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public void delete(UUID id) {
        SourceFile sourceFile = sourceFileRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Source file not found with id: " + id));
        sourceFileRepository.delete(sourceFile);
    }

    @Override
    public List<SourceFileResponse> getByAnalysisId(UUID analysisId) {
        return sourceFileRepository.findByAnalysisId(analysisId).stream()
                .map(dtoMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<SourceFileResponse> getByPackageName(String packageName) {
        return sourceFileRepository.findByPackageName(packageName).stream()
                .map(dtoMapper::toResponse)
                .collect(Collectors.toList());
    }

}
