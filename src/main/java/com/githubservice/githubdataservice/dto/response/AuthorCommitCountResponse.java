package com.githubservice.githubdataservice.dto.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AuthorCommitCountResponse {
    private String authorName;
    private Long commitCount;
}
