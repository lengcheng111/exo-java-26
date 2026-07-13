package com.example.exoservice.service.consumer;

import com.example.exoservice.dto.Inconsistency;
import reactor.core.publisher.Mono;

import java.util.List;

public interface ComparisonService {
    Mono<List<Inconsistency>> compare(String email);
}
