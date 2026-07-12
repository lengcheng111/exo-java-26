//package com.example.exoservice.service;
//
//import com.example.exoservice.dto.JobState;
//import com.example.exoservice.dto.ResultMessage;
//import org.springframework.stereotype.Service;
//
//import java.util.Map;
//import java.util.concurrent.ConcurrentHashMap;
//import java.util.concurrent.CopyOnWriteArrayList;
//import java.util.concurrent.atomic.AtomicInteger;
//
//@Service
//public class JobStoreServiceImpl {
//
//    private final Map<String, JobState> jobs = new ConcurrentHashMap<>();
//
//    public void createJob(String jobId, int total) {
//        JobState state = new JobState();
//        state.setTotal(total);
//        state.setResults(new CopyOnWriteArrayList<>());
//        state.setDone(new AtomicInteger());
//
//        jobs.put(jobId, state);
//    }
//
//    public void add(ResultMessage result) {
//        JobState state = jobs.get(result.jobId());
//
//        if (state == null) {
//            throw new IllegalArgumentException(
//                    "Job not found: " + result.jobId()
//            );
//        }
//
//        state.getResults().add(result);
//        state.getDone().incrementAndGet();
//    }
//
//    public JobState get(String jobId) {
//        return jobs.get(jobId);
//    }
//}
