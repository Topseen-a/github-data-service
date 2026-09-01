package com.githubservice.githubdataservice.service;

import com.githubservice.githubdataservice.dto.response.AuthorCommitCountResponse;
import com.githubservice.githubdataservice.dto.response.CommitResponse;
import com.githubservice.githubdataservice.dto.response.RepoResponse;
import com.githubservice.githubdataservice.exception.RepoNotFoundException;
import com.githubservice.githubdataservice.model.Repo;
import com.githubservice.githubdataservice.repository.CommitRepository;
import com.githubservice.githubdataservice.repository.RepoRepository;
import com.githubservice.githubdataservice.repository.projection.AuthorCommitCount;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class RepoServiceImpl implements RepoService {
    private final RepoRepository repoRepository;
    private final CommitRepository commitRepository;
    private final ModelMapper modelMapper;

    @Override
    public List<RepoResponse> getAllRepos() {
        return repoRepository.findAll().stream()
                .map(repo -> modelMapper.map(repo, RepoResponse.class))
                .toList();
    }

    @Override
    public RepoResponse getRepo(String owner, String name) throws RepoNotFoundException {
        return modelMapper.map(getRepoBy(owner, name), RepoResponse.class);
    }

    @Override
    public List<CommitResponse> getCommits(String owner, String name, int page, int size) throws RepoNotFoundException {
        getRepoBy(owner, name);
        if (page < 0) page = 0;
        if (size < 1 || size > 100) size = 20;
        Pageable pageable = PageRequest.of(page, size);
        return commitRepository.findByRepo_OwnerAndRepo_NameOrderByCommittedAtDesc(owner, name, pageable)
                .stream()
                .map(commit -> modelMapper.map(commit, CommitResponse.class))
                .toList();
    }

    @Override
    public List<AuthorCommitCountResponse> getTopAuthors(String owner, String name, int limit) throws RepoNotFoundException {
        getRepoBy(owner, name);
        if (limit < 1 || limit > 100) limit = 10;
        Pageable pageable = PageRequest.of(0, limit);
        List<AuthorCommitCount> results = commitRepository.findTopAuthorsByRepo(owner, name, pageable);
        return results.stream()
                .map(result -> {
                    AuthorCommitCountResponse response = new AuthorCommitCountResponse();
                    response.setAuthorName(result.getAuthorName());
                    response.setCommitCount(result.getCommitCount());
                    return response;
                })
                .toList();
    }

    private Repo getRepoBy(String owner, String name) throws RepoNotFoundException {
        return repoRepository.findByOwnerAndName(owner, name)
                .orElseThrow(() -> new RepoNotFoundException(
                        String.format("repository %s/%s not found", owner, name)));
    }
}
