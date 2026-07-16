package com.omerkoc.main.controller.impl;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.omerkoc.main.controller.IIssueController;
import com.omerkoc.main.model.Issue;
import com.omerkoc.main.service.IIssueService;

import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/issues")
@RequiredArgsConstructor
public class IssueControllerImpl implements IIssueController {

    private final IIssueService issueService;

    @PostMapping
    @Override
    public ResponseEntity<Issue> create(@RequestBody Issue issue) {
        return new ResponseEntity<>(issueService.create(issue), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @Override
    public ResponseEntity<Issue> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(issueService.getById(id));
    }

    @GetMapping
    @Override
    public ResponseEntity<List<Issue>> getAll() {
        return ResponseEntity.ok(issueService.getAll());
    }

    @DeleteMapping("/{id}")
    @Override
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        issueService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/analysis/{analysisId}")
    @Override
    public ResponseEntity<List<Issue>> getByAnalysisId(@PathVariable UUID analysisId) {
        return ResponseEntity.ok(issueService.getByAnalysisId(analysisId));
    }

    @GetMapping("/source-file/{sourceFileId}")
    @Override
    public ResponseEntity<List<Issue>> getBySourceFileId(@PathVariable UUID sourceFileId) {
        return ResponseEntity.ok(issueService.getBySourceFileId(sourceFileId));
    }

    @GetMapping("/severity/{severity}")
    @Override
    public ResponseEntity<List<Issue>> getBySeverity(@PathVariable String severity) {
        return ResponseEntity.ok(issueService.getBySeverity(severity));
    }

    @GetMapping("/category/{category}")
    @Override
    public ResponseEntity<List<Issue>> getByCategory(@PathVariable String category) {
        return ResponseEntity.ok(issueService.getByCategory(category));
    }

}
