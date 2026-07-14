package com.omerkoc.main.controller.impl;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.omerkoc.main.controller.IAnalysisController;
import com.omerkoc.main.model.Analysis;
import com.omerkoc.main.service.IAnalysisService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;

@RestController
@RequestMapping("/analysis")
@RequiredArgsConstructor
public class AnalysisControllerImpl implements IAnalysisController {

    private final IAnalysisService analysisService;

    @Override
    public ResponseEntity<Analysis> getById(Long id) {
        return ResponseEntity.ok(analysisService.getById(id));
    }

}
