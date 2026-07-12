package com.example.exoservice.controller;

import com.example.exoservice.service.AggregatorService;
import com.example.exoservice.dto.JobState;
import com.example.exoservice.service.InconsistencyService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ExoController {

    private final InconsistencyService producerService;
    private final AggregatorService aggregatorService;

    public ExoController(InconsistencyService producerService, AggregatorService aggregatorService) {
        this.producerService = producerService;
        this.aggregatorService = aggregatorService;
    }

    @GetMapping("/inconsistencies")
    Test getInConsistencies() {
        String jobId = producerService.getInconsistency();
        return new Test(jobId, "RUNNING");
    }

    @GetMapping("/inconsistencies/{jobId}")
    public ResponseEntity<JobState> getJob(@PathVariable String jobId) {
        JobState job = aggregatorService.get(jobId);

        return job == null
                ? ResponseEntity.notFound().build()
                : ResponseEntity.ok(job);
    }

    record Test(String id, String email){}
}
