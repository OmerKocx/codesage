package com.omerkoc.main.controller.impl;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.omerkoc.main.controller.IAnalysisController;
import com.omerkoc.main.dto.AnalysisRequest;
import com.omerkoc.main.dto.AnalysisResponse;
import com.omerkoc.main.service.IAnalysisService;

import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/analysis")
@RequiredArgsConstructor
public class AnalysisControllerImpl implements IAnalysisController {

    private final IAnalysisService analysisService;

    @PostMapping
    @Override
    public ResponseEntity<AnalysisResponse> create(@Valid @RequestBody AnalysisRequest request) {
        return new ResponseEntity<>(analysisService.create(request), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @Override
    public ResponseEntity<AnalysisResponse> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(analysisService.getById(id));
    }

    @GetMapping
    @Override
    public ResponseEntity<List<AnalysisResponse>> getAll() {
        return ResponseEntity.ok(analysisService.getAll());
    }

    @DeleteMapping("/{id}")
    @Override
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        analysisService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/repo")
    @Override
    public ResponseEntity<List<AnalysisResponse>> getByRepoUrl(@RequestParam String url) {
        return ResponseEntity.ok(analysisService.getByRepoUrl(url));
    }

    @GetMapping("/status/{status}")
    @Override
    public ResponseEntity<List<AnalysisResponse>> getByStatus(@PathVariable String status) {
        return ResponseEntity.ok(analysisService.getByStatus(status));
    }

}
