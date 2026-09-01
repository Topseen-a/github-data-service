package com.githubservice.githubdataservice.integration.github;

import com.githubservice.githubdataservice.dto.response.GitHubRepoResponse;

import java.time.Instant;

public interface GitHubApiService {
    GitHubRepoResponse fetchRepo(String owner, String name);
    GitHubCommitPage fetchCommits(String owner, String name, Instant since, int page, int perPage);
}
