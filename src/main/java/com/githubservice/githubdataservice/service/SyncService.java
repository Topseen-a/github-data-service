package com.githubservice.githubdataservice.service;

import com.githubservice.githubdataservice.dto.response.SyncResultResponse;
import com.githubservice.githubdataservice.exception.RepoNotFoundException;
import com.githubservice.githubdataservice.model.Repo;

import java.time.Instant;

public interface SyncService {
    Repo ensureRepo(String owner, String name);
    SyncResultResponse syncRepo(Repo repo);
    SyncResultResponse triggerSync(String owner, String name);
    void resetSync(String owner, String name, Instant since, boolean purgeExisting) throws RepoNotFoundException;
    void syncAllRepos();
}
