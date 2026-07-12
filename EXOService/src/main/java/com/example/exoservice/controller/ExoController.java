package com.example.exoservice.controller;

import com.example.exoservice.dto.JobState;
import com.example.exoservice.service.AggregatorService;
import com.example.exoservice.service.InconsistencyService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.concurrent.TimeoutException;

@RestController
public class ExoController {

    private final InconsistencyService producerService;
    private final AggregatorService aggregatorService;

    public ExoController(InconsistencyService producerService, AggregatorService aggregatorService) {
        this.producerService = producerService;
        this.aggregatorService = aggregatorService;
    }

//    @PostMapping("/inconsistencies")
//    Result getInConsistencies() {
//        String jobId = producerService.getInconsistency();
//        return new Result(jobId, "RUNNING");
//    }

//    @GetMapping("/inconsistencies/{jobId}")
//    public ResponseEntity<JobState> getJob(@PathVariable String jobId) {
//        JobState job = aggregatorService.get(jobId);
//
//        return job == null
//                ? ResponseEntity.notFound().build()
//                : ResponseEntity.ok(job);
//    }

    @GetMapping("/inconsistencies")
    public Mono<ResponseEntity<JobState>> getInconsistencies() {
        return producerService.getInconsistency()
                .flatMap(jobId ->
                        Flux.interval(Duration.ofSeconds(1))
                                .concatMap(i -> aggregatorService.get(jobId))
                                .next())
                .map(ResponseEntity::ok)
                .timeout(Duration.ofMinutes(2))
                .onErrorReturn(
                        TimeoutException.class,
                        ResponseEntity.status(HttpStatus.REQUEST_TIMEOUT).build()
                );
    }
}
