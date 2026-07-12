package com.example.exoservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResultMessage {
    private String jobId;
    private String email;
    private ResultMessageStatus status;
    private List<String> inconsistencies;
}