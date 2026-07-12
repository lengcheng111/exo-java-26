package com.example.exoservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class JobState {

    int total;

    List<ResultMessage> results = new CopyOnWriteArrayList<>();
}
