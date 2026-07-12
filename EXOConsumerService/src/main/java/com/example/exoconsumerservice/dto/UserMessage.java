package com.example.exoconsumerservice.dto;

public class UserMessage {

    private String jobId;
    private String email;

    public UserMessage() {
    }

    public UserMessage(String jobId, String email) {
        this.jobId = jobId;
        this.email = email;
    }

    public String getJobId() {
        return jobId;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
