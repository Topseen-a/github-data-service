package com.githubservice.githubdataservice.controller;

import com.githubservice.githubdataservice.dto.response.AuthorCommitCountResponse;
import com.githubservice.githubdataservice.dto.response.CommitResponse;
import com.githubservice.githubdataservice.dto.response.RepoResponse;
import com.githubservice.githubdataservice.exception.RepoNotFoundException;
import com.githubservice.githubdataservice.service.RepoService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/repos")
@AllArgsConstructor
public class RepoController {
    private final RepoService repoService;

    @GetMapping
    public List<RepoResponse> getAllRepos() {
        return repoService.getAllRepos();
    }

    @GetMapping("/{owner}/{name}")
    public RepoResponse getRepo(@PathVariable String owner, @PathVariable String name) throws RepoNotFoundException {
        return repoService.getRepo(owner, name);
    }

    @GetMapping("/{owner}/{name}/commits")
    public List<CommitResponse> getCommits(
            @PathVariable String owner,
            @PathVariable String name,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) throws RepoNotFoundException {
        return repoService.getCommits(owner, name, page, size);
    }

    @GetMapping("/{owner}/{name}/authors/top")
    public List<AuthorCommitCountResponse> getTopAuthors(
            @PathVariable String owner,
            @PathVariable String name,
            @RequestParam(defaultValue = "10") int limit) throws RepoNotFoundException {
        return repoService.getTopAuthors(owner, name, limit);
    }
}
