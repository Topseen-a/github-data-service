package com.githubservice.githubdataservice.dto.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SyncResultResponse {
    private String repoId;
    private int commitsSaved;
    private int pagesFetched;
    private boolean backfillComplete;
}
