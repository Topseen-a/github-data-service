package com.githubservice.githubdataservice.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class GitHubCommitResponse {
    private String sha;
    private GitHubCommitDetails commit;
    @JsonProperty("html_url")
    private String htmlUrl;
}
