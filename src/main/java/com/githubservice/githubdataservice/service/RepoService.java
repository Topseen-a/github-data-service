package com.githubservice.githubdataservice.service;

import com.githubservice.githubdataservice.dto.response.AuthorCommitCountResponse;
import com.githubservice.githubdataservice.dto.response.CommitResponse;
import com.githubservice.githubdataservice.dto.response.RepoResponse;
import com.githubservice.githubdataservice.exception.RepoNotFoundException;

import java.util.List;

public interface RepoService {
    List<RepoResponse> getAllRepos();
    RepoResponse getRepo(String owner, String name) throws RepoNotFoundException;
    List<CommitResponse> getCommits(String owner, String name, int page, int size) throws RepoNotFoundException;
    List<AuthorCommitCountResponse> getTopAuthors(String owner, String name, int limit) throws RepoNotFoundException;
}
