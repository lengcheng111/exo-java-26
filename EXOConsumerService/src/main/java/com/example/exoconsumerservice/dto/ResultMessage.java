package com.example.exoconsumerservice.dto;

import java.util.List;

public record ResultMessage(
        String jobId,
        String email,
        ResultMessageStatus status,
        List<String> inconsistencies
) {}