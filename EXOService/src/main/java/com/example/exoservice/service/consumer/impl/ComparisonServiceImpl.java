package com.example.exoservice.service.consumer.impl;

import com.example.exoservice.config.UserApiClient;
import com.example.exoservice.dto.FolderResponse;
import com.example.exoservice.dto.Inconsistency;
import com.example.exoservice.dto.UserFolderResponse;
import com.example.exoservice.service.consumer.ComparisonService;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class ComparisonServiceImpl implements ComparisonService {

    private final UserApiClient apiClient;

    public ComparisonServiceImpl(UserApiClient apiClient) {
        this.apiClient = apiClient;
    }

    @Override
    public List<Inconsistency> compare(String email) {
        List<UserFolderResponse> userFolders = apiClient.getUserFolders(email);

        // TODO: if this api does not change, we should cache this response of this API
        List<FolderResponse> globalFolders = apiClient.getFolders()
                .stream()
                .filter(folder -> email.equalsIgnoreCase(folder.user()))
                .toList();

        Map<String, UserFolderResponse> userFolderMap = userFolders.stream()
                .collect(Collectors.toMap(
                        UserFolderResponse::id,
                        Function.identity()
                ));

        Map<String, FolderResponse> globalFolderMap = globalFolders.stream()
                .collect(Collectors.toMap(
                        FolderResponse::id,
                        Function.identity()
                ));

        List<Inconsistency> results = new LinkedList<>();

        for (FolderResponse globalFolder : globalFolders) {
            UserFolderResponse userFolder =
                    userFolderMap.get(globalFolder.id());

            if (userFolder == null) {
                results.add(new Inconsistency(
                        globalFolder.id(),
                        "MISSING_IN_USER",
                        "Folder exists globally but is missing for user"
                ));
            } else if (!globalFolder.name().equals(userFolder.name())) {
                results.add(new Inconsistency(
                        globalFolder.id(),
                        "NAME_MISMATCH",
                        "Global name: %s, user name: %s"
                                .formatted(
                                        globalFolder.name(),
                                        userFolder.name()
                                )
                ));
            }
        }

        for (UserFolderResponse userFolder : userFolders) {
            if (!globalFolderMap.containsKey(userFolder.id())) {
                results.add(new Inconsistency(
                        userFolder.id(),
                        "MISSING_IN_GLOBAL",
                        "Folder exists for user but is missing globally"
                ));
            }
        }

        return results;
    }
}