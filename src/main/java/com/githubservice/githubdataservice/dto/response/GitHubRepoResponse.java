package com.githubservice.githubdataservice.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class GitHubRepoResponse {
    private String name;
    private String description;
    @JsonProperty("html_url")
    private String htmlUrl;
    private String language;
    @JsonProperty("forks_count")
    private Integer forksCount;
    @JsonProperty("stargazers_count")
    private Integer starsCount;
    @JsonProperty("open_issues_count")
    private Integer openIssuesCount;
    @JsonProperty("watchers_count")
    private Integer watchersCount;
    @JsonProperty("created_at")
    private Instant githubCreatedAt;
    @JsonProperty("updated_at")
    private Instant githubUpdatedAt;
}
