package com.githubservice.githubdataservice.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
public class CommitResponse {
    private String sha;
    private String message;
    private String authorName;
    private String authorEmail;
    private Instant committedAt;
    private String htmlUrl;
}
