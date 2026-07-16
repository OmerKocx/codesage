package com.omerkoc.main.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.omerkoc.main.model.SourceFile;

@Repository
public interface SourceFileRepository extends JpaRepository<SourceFile, UUID> {
    List<SourceFile> findByAnalysisId(UUID analysisId);
    List<SourceFile> findByPackageName(String packageName);
}
