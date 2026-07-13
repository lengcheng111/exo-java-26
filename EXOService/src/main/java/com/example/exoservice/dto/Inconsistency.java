package com.example.exoservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Inconsistency {

    private String folderId;
    private String type;
    private String message;
}