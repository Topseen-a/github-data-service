package com.githubservice.githubdataservice.integration.github;

import com.githubservice.githubdataservice.dto.response.GitHubCommitResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@Getter
@Setter
public class GitHubCommitPage {
    private List<GitHubCommitResponse> commits;
    private boolean hasNext;
}
