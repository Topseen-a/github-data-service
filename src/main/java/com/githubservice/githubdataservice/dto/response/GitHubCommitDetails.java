package com.githubservice.githubdataservice.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class GitHubCommitDetails {
    private String message;
    private GitHubCommitAuthor author;
}
