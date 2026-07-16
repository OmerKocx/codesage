package com.omerkoc.main.controller.impl;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.omerkoc.main.controller.ISourceFileController;
import com.omerkoc.main.dto.SourceFileRequest;
import com.omerkoc.main.dto.SourceFileResponse;
import com.omerkoc.main.service.ISourceFileService;

import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/source-files")
@RequiredArgsConstructor
public class SourceFileControllerImpl implements ISourceFileController {

    private final ISourceFileService sourceFileService;

    @PostMapping
    @Override
    public ResponseEntity<SourceFileResponse> create(@Valid @RequestBody SourceFileRequest request) {
        return new ResponseEntity<>(sourceFileService.create(request), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @Override
    public ResponseEntity<SourceFileResponse> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(sourceFileService.getById(id));
    }

    @GetMapping
    @Override
    public ResponseEntity<List<SourceFileResponse>> getAll() {
        return ResponseEntity.ok(sourceFileService.getAll());
    }

    @DeleteMapping("/{id}")
    @Override
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        sourceFileService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/analysis/{analysisId}")
    @Override
    public ResponseEntity<List<SourceFileResponse>> getByAnalysisId(@PathVariable UUID analysisId) {
        return ResponseEntity.ok(sourceFileService.getByAnalysisId(analysisId));
    }

    @GetMapping("/package")
    @Override
    public ResponseEntity<List<SourceFileResponse>> getByPackageName(@RequestParam String name) {
        return ResponseEntity.ok(sourceFileService.getByPackageName(name));
    }

}
