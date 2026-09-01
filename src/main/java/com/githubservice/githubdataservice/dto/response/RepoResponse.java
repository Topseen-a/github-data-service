package com.githubservice.githubdataservice.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
public class RepoResponse {
    private String id;
    private String owner;
    private String name;
    private String description;
    private String htmlUrl;
    private String language;
    private Integer forksCount;
    private Integer starsCount;
    private Integer openIssuesCount;
    private Integer watchersCount;
    private Instant githubCreatedAt;
    private Instant githubUpdatedAt;
    private Instant syncSince;
    private Boolean backfillComplete;
    private Instant lastSyncedAt;
}
