package com.omerkoc.main.service.impl;

import org.springframework.stereotype.Service;

import com.omerkoc.main.model.Analysis;
import com.omerkoc.main.repository.AnalysisRepository;
import com.omerkoc.main.service.IAnalysisService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AnalysisServiceImpl implements IAnalysisService {

    private final AnalysisRepository analysisRepository;

    @Override
    public Analysis getById(Long id) {
        return null;
    }

}
