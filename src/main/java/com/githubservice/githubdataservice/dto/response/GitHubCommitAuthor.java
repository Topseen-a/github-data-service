package com.githubservice.githubdataservice.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class GitHubCommitAuthor {
    private String name;
    private String email;
    private Instant date;
}
