package com.omerkoc.main.controller.impl;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.omerkoc.main.controller.ISourceFileController;
import com.omerkoc.main.model.SourceFile;
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
    public ResponseEntity<SourceFile> create(@RequestBody SourceFile sourceFile) {
        return new ResponseEntity<>(sourceFileService.create(sourceFile), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @Override
    public ResponseEntity<SourceFile> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(sourceFileService.getById(id));
    }

    @GetMapping
    @Override
    public ResponseEntity<List<SourceFile>> getAll() {
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
    public ResponseEntity<List<SourceFile>> getByAnalysisId(@PathVariable UUID analysisId) {
        return ResponseEntity.ok(sourceFileService.getByAnalysisId(analysisId));
    }

    @GetMapping("/package")
    @Override
    public ResponseEntity<List<SourceFile>> getByPackageName(@RequestParam String name) {
        return ResponseEntity.ok(sourceFileService.getByPackageName(name));
    }

}
