package com.omerkoc.main.service.impl;

import org.springframework.stereotype.Service;

import com.omerkoc.main.exceptions.NotFoundException;
import com.omerkoc.main.model.SourceFile;
import com.omerkoc.main.repository.SourceFileRepository;
import com.omerkoc.main.service.ISourceFileService;

import lombok.RequiredArgsConstructor;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SourceFileServiceImpl implements ISourceFileService {

    private final SourceFileRepository sourceFileRepository;

    @Override
    public SourceFile create(SourceFile sourceFile) {
        return sourceFileRepository.save(sourceFile);
    }

    @Override
    public SourceFile getById(UUID id) {
        return sourceFileRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Source file not found with id: " + id));
    }

    @Override
    public List<SourceFile> getAll() {
        return sourceFileRepository.findAll();
    }

    @Override
    public void delete(UUID id) {
        SourceFile sourceFile = getById(id);
        sourceFileRepository.delete(sourceFile);
    }

    @Override
    public List<SourceFile> getByAnalysisId(UUID analysisId) {
        return sourceFileRepository.findByAnalysisId(analysisId);
    }

    @Override
    public List<SourceFile> getByPackageName(String packageName) {
        return sourceFileRepository.findByPackageName(packageName);
    }

}
