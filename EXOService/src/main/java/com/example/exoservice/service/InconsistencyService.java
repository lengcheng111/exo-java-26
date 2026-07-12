package com.example.exoservice.service;

import reactor.core.publisher.Mono;

public interface InconsistencyService {

    /**
     * fetch all user then push all user to Kafka
     *
     * @return
     */
    Mono<String> getInconsistency();

}
