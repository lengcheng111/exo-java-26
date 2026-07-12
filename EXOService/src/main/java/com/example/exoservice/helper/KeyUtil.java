package com.example.exoservice.helper;

public class KeyUtil {
    public static final String SUFFIX_DLT_TOPIC = ".DLT";

    private KeyUtil(){}

    public static String makeJobKey(String jobId) {
        return "job:" + jobId;
    }

    public static String makeResultKey(String jobId) {
        return "job:" + jobId + ":results";
    }
}
