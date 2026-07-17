package com.omerkoc.main.dto;

import java.util.List;

import lombok.Builder;

@Builder
public record GeminiResponse(List<Candidate> candidates) {
    public record Candidate(Content content) {
    }

    public record Content(List<Part> parts) {
    }

    public record Part(String text) {
    }
}
