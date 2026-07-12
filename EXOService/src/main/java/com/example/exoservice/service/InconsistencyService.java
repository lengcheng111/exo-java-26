package com.example.exoservice.service;

public interface InconsistencyService {

    /**
     * fetch all user then push all user to Kafka
     * @return
     */
    public String getInconsistency();

}
