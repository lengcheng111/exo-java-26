package com.example.exoservice.service.consumer;

import com.example.exoservice.dto.Inconsistency;

import java.util.List;

public interface ComparisonService {
    List<Inconsistency> compare(String email);
}
