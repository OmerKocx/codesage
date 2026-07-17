package com.omerkoc.main.service.impl;

import com.omerkoc.main.dto.GeminiRequest;
import com.omerkoc.main.dto.GeminiResponse;
import com.omerkoc.main.dto.GeminiAnalysisResult;
import com.omerkoc.main.service.IGeminiService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import java.util.List;

@Service
@Slf4j
public class GeminiServiceImpl implements IGeminiService {

    @Value("${GEMINI_API_KEY}")
    private String apiKey;

    @Value("${API_URL}")
    private String apiUrl;

    private final WebClient webClient;
    private final ObjectMapper objectMapper;

    public GeminiServiceImpl() {
        org.springframework.web.util.DefaultUriBuilderFactory factory = new org.springframework.web.util.DefaultUriBuilderFactory();
        factory.setEncodingMode(org.springframework.web.util.DefaultUriBuilderFactory.EncodingMode.NONE);
        this.webClient = WebClient.builder()
                .uriBuilderFactory(factory)
                .build();
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public GeminiAnalysisResult analyzeCodebase(String mergedCodeText) {
        log.info("Sending codebase to Gemini API for static analysis...");

        // Construct the prompt with rules demanding a structured JSON response
        String systemPrompt = "You are an expert static code analysis tool. " +
                "Analyze the provided codebase files for security vulnerabilities, bugs, performance bottlenecks, and style issues. "
                +
                "You must return the analysis strictly as a single JSON object. " +
                "Do not include any explanation, introductory text, or markdown code block formatting (like ```json). Just the raw JSON. "
                +
                "The JSON schema must be exactly as follows:\n" +
                "{\n" +
                "  \"overallScore\": <integer between 0 and 100 representing overall code health>,\n" +
                "  \"issues\": [\n" +
                "    {\n" +
                "      \"filePath\": \"<relative file path>\",\n" +
                "      \"lineNumber\": <line number as integer>,\n" +
                "      \"severity\": \"<HIGH | MEDIUM | LOW>\",\n" +
                "      \"category\": \"<SECURITY | BUG | PERFORMANCE | STYLE>\",\n" +
                "      \"title\": \"<short descriptive title>\",\n" +
                "      \"description\": \"<detailed explanation of the issue>\",\n" +
                "      \"suggestion\": \"<clear code suggestion or action to fix the issue>\"\n" +
                "    }\n" +
                "  ]\n" +
                "}\n\n" +
                "Here is the codebase:\n" + mergedCodeText;

        try {
            // Build the nested payload required by Gemini API
            GeminiRequest.Part part = new GeminiRequest.Part(systemPrompt);
            GeminiRequest.Content content = new GeminiRequest.Content(List.of(part));
            GeminiRequest requestPayload = new GeminiRequest(List.of(content));

            // Call Gemini API via WebClient
            String targetUrl = apiUrl + "?key=" + apiKey;
            log.info("Gemini API target URL: {}", targetUrl);
            GeminiResponse response;
            try {
                response = webClient.post()
                        .uri(targetUrl)
                        .header("Content-Type", "application/json")
                        .bodyValue(requestPayload)
                        .retrieve()
                        .bodyToMono(GeminiResponse.class)
                        .block();
            } catch (org.springframework.web.reactive.function.client.WebClientResponseException e) {
                log.error("Gemini API Error Response Body: {}", e.getResponseBodyAsString());
                throw new RuntimeException("Failed to analyze codebase using Gemini API: " + e.getStatusCode() + " "
                        + e.getStatusText() + " - " + e.getResponseBodyAsString(), e);
            }

            if (response == null || response.candidates() == null || response.candidates().isEmpty()) {
                throw new RuntimeException("Received empty or invalid response from Gemini API");
            }

            // Extract the generated text block containing the JSON
            String rawText = response.candidates().get(0).content().parts().get(0).text();
            if (rawText == null || rawText.isBlank()) {
                throw new RuntimeException("Generated content text is empty");
            }

            // Clean up markdown block wrapper if Gemini returned it (e.g. ```json ... ```)
            String cleanJson = rawText.trim();
            if (cleanJson.startsWith("```json")) {
                cleanJson = cleanJson.substring(7);
            } else if (cleanJson.startsWith("```")) {
                cleanJson = cleanJson.substring(3);
            }
            if (cleanJson.endsWith("```")) {
                cleanJson = cleanJson.substring(0, cleanJson.length() - 3);
            }
            cleanJson = cleanJson.trim();

            log.info("Gemini analysis response received successfully. Parsing JSON...");

            // Map the JSON string into GeminiAnalysisResult DTO
            return objectMapper.readValue(cleanJson, GeminiAnalysisResult.class);

        } catch (Exception e) {
            log.error("Error during Gemini API communication or parsing: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to analyze codebase using Gemini API: " + e.getMessage(), e);
        }
    }
}
