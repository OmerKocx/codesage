package com.omerkoc.main.service.impl;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.stream.Stream;

import org.eclipse.jgit.api.Git;
import org.springframework.stereotype.Service;

import com.omerkoc.main.service.IGitService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class GitServiceImpl implements IGitService {

    @Override
    public Path cloneRepository(String repoUrl) {
        try {
            String repoName = repoUrl.substring(repoUrl.lastIndexOf("/") + 1)
                    .replace(".git", "");
            Path tempDir = Files.createTempDirectory(repoName + "-");
            try (Git git = Git.cloneRepository()
                    .setURI(repoUrl)
                    .setDirectory(tempDir.toFile())
                    .call()) {

                return tempDir;
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to clone repository: " + e.getMessage(), e);
        }

    }

    @Override
    public String mergeFilesToText(Path tempDir) {
        StringBuilder mergedContent = new StringBuilder();

        // Files.walk işletim sistemi kaynaklarını kullandığı için try-with-resources
        // ile açılmalıdır
        try (Stream<Path> paths = Files.walk(tempDir)) {
            paths.filter(Files::isRegularFile) // Sadece dosyaları al
                    .filter(path -> !path.toString().contains(".git")) // Git klasörünü atla
                    .filter(path -> {
                        // Sadece analiz etmek istediğimiz uzantıları seçiyoruz
                        String fileName = path.getFileName().toString().toLowerCase();
                        return fileName.endsWith(".java") ||
                                fileName.endsWith(".py") ||
                                fileName.endsWith(".js") ||
                                fileName.endsWith(".ts") ||
                                fileName.endsWith(".xml") ||
                                fileName.endsWith(".yml") ||
                                fileName.endsWith(".html") ||
                                fileName.endsWith(".css") ||
                                fileName.endsWith(".scss") ||
                                fileName.endsWith(".php") ||
                                fileName.endsWith(".config") ||
                                fileName.endsWith(".md") ||
                                fileName.endsWith(".txt") ||
                                fileName.endsWith(".xml") ||
                                fileName.endsWith(".svg") ||
                                fileName.endsWith(".json") ||
                                fileName.endsWith(".env") ||
                                fileName.endsWith(".sql") ||
                                fileName.endsWith(".properties");
                    })
                    .forEach(path -> {
                        try {
                            // Göreceli (relative) dosya yolunu buluyoruz (Örn: src/main/java/Main.java)
                            String relativePath = tempDir.relativize(path).toString();

                            // Dosya içeriğini okuyoruz
                            String fileContent = Files.readString(path);

                            // StringBuilder'a anlamlı bir şekilde ekliyoruz
                            mergedContent.append("--- FILE: ").append(relativePath).append(" ---\n");
                            mergedContent.append(fileContent).append("\n");
                            mergedContent.append("----------------------------------------\n\n");
                        } catch (IOException e) {
                            // If an error occurs while reading a single file, we throw a RuntimeException
                            throw new RuntimeException("Failed to read file: " + path, e);
                        }
                    });

        } catch (IOException e) {
            throw new RuntimeException("Failed to scan directory: " + e.getMessage(), e);
        }

        return mergedContent.toString();
    }

    @Override
    public void cleanUp(Path tempDir) {
        if (tempDir == null)
            return;

        try (Stream<Path> walk = Files.walk(tempDir)) {
            walk.sorted(Comparator.reverseOrder()) // Reverse sort to delete deepest files first
                    .map(Path::toFile)
                    .forEach(File::delete); // Delete each file/directory

            log.info("Temporary directory successfully cleaned up: " + tempDir);
        } catch (IOException e) {
            // Even if deletion fails, we don't want the rest of the application to crash,
            // just log it
            log.error("Error occurred while deleting temporary directory: " + e.getMessage());
        }
    }

}
