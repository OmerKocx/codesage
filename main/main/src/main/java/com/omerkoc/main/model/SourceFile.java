package com.omerkoc.main.model;

import jakarta.persistence.*;
import lombok.*;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "source_file")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SourceFile {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @NotNull(message = "Analysis cannot be null")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "analysis_id", nullable = false)
    private Analysis analysis;

    @NotBlank(message = "File path cannot be blank")
    @Column(name = "file_path", length = 1000, nullable = false)
    private String filePath;

    @Column(name = "package_name")
    private String packageName;

    @Column(name = "class_name")
    private String className;

    @Builder.Default
    @OneToMany(mappedBy = "sourceFile", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Issue> issues = new ArrayList<>();
}
