package com.example.exoconsumerservice.service;

import com.example.exoconsumerservice.dto.Inconsistency;

import java.util.List;

public interface ComparisonService {
    List<Inconsistency> compare(String email);
}
