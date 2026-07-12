package com.example.exoconsumerservice.dto;

public record Inconsistency(
        String folderId,
        String type,
        String message
) {
}
