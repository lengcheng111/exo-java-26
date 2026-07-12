package com.example.exoservice.dto;

public record Inconsistency(
        String folderId,
        String type,
        String message
) {
}
