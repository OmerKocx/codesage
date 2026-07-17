package com.omerkoc.main.dto;

import java.util.List;
import lombok.Builder;

@Builder
public record GeminiRequest(List<Content> contents) {

    public record Content(List<Part> parts) {
    }

    public record Part(String text) {
    }
}
