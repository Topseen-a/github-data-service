package com.githubservice.githubdataservice.service;

import com.githubservice.githubdataservice.config.GitHubRepoConfig;
import com.githubservice.githubdataservice.config.GitHubSyncConfig;
import com.githubservice.githubdataservice.dto.response.GitHubCommitResponse;
import com.githubservice.githubdataservice.dto.response.GitHubRepoResponse;
import com.githubservice.githubdataservice.dto.response.SyncResultResponse;
import com.githubservice.githubdataservice.exception.GitHubApiException;
import com.githubservice.githubdataservice.exception.RepoNotFoundException;
import com.githubservice.githubdataservice.integration.github.GitHubApiService;
import com.githubservice.githubdataservice.integration.github.GitHubCommitPage;
import com.githubservice.githubdataservice.model.Commit;
import com.githubservice.githubdataservice.model.Repo;
import com.githubservice.githubdataservice.repository.CommitRepository;
import com.githubservice.githubdataservice.repository.RepoRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
@Slf4j
@AllArgsConstructor
public class SyncServiceImpl implements SyncService {
    private final GitHubApiService gitHubApiService;
    private final RepoRepository repoRepository;
    private final CommitRepository commitRepository;
    private final GitHubRepoConfig gitHubRepoConfig;
    private final GitHubSyncConfig gitHubSyncConfig;

    @Override
    public Repo ensureRepo(String owner, String name) {
        return repoRepository.findByOwnerAndName(owner, name)
                .orElseGet(() -> registerRepo(owner, name));
    }

    private Repo registerRepo(String owner, String name) {
        GitHubRepoResponse remote = gitHubApiService.fetchRepo(owner, name);
        Repo repo = new Repo();
        repo.setOwner(owner);
        repo.setName(name);
        applyMetadata(repo, remote);
        repo.setSyncSince(gitHubRepoConfig.getStartDate());
        repo.setSyncCursorPage(1);
        repo.setBackfillComplete(false);
        Repo saved = repoRepository.save(repo);
        log.info("registered repo {}/{}, pulling commits since {}", owner, name, saved.getSyncSince());
        return saved;
    }

    @Override
    public SyncResultResponse triggerSync(String owner, String name) {
        return syncRepo(ensureRepo(owner, name));
    }

    @Override
    public SyncResultResponse syncRepo(Repo repo) {
        refreshMetadata(repo);

        int perPage = gitHubSyncConfig.getPerPage();
        int maxPagesThisRun = gitHubSyncConfig.getMaxPagesPerRun();

        int page = Boolean.TRUE.equals(repo.getBackfillComplete()) ? 1 : repo.getSyncCursorPage();
        int pagesFetched = 0;
        int commitsSaved = 0;
        Instant newestCommitSeen = repo.getSyncSince();
        boolean reachedEndOfHistory = false;

        while (pagesFetched < maxPagesThisRun) {
            GitHubCommitPage result = gitHubApiService.fetchCommits(
                    repo.getOwner(), repo.getName(), repo.getSyncSince(), page, perPage);
            pagesFetched++;

            if (result.getCommits().isEmpty()) {
                reachedEndOfHistory = true;
                break;
            }

            for (GitHubCommitResponse remote : result.getCommits()) {
                if (remote.getSha() == null || commitRepository.existsByRepoIdAndSha(repo.getId(), remote.getSha())) {
                    continue;
                }
                Commit commit = toEntity(remote, repo);
                if (commit.getCommittedAt() == null) {
                    log.warn("skipping commit {} for {}/{}: missing author date", remote.getSha(), repo.getOwner(), repo.getName());
                    continue;
                }
                commitRepository.save(commit);
                commitsSaved++;
                if (commit.getCommittedAt().isAfter(newestCommitSeen)) {
                    newestCommitSeen = commit.getCommittedAt();
                }
            }

            if (!result.isHasNext()) {
                reachedEndOfHistory = true;
                break;
            }
            page++;
        }

        if (reachedEndOfHistory) {
            repo.setBackfillComplete(true);
            repo.setSyncCursorPage(1);
            repo.setSyncSince(newestCommitSeen);
        } else {
            repo.setSyncCursorPage(page);
        }
        repo.setLastSyncedAt(Instant.now());
        repoRepository.save(repo);

        log.info("synced {}/{}: {} new commit(s) across {} page(s), backfillComplete={}",
                repo.getOwner(), repo.getName(), commitsSaved, pagesFetched, repo.getBackfillComplete());

        SyncResultResponse response = new SyncResultResponse();
        response.setRepoId(repo.getId());
        response.setCommitsSaved(commitsSaved);
        response.setPagesFetched(pagesFetched);
        response.setBackfillComplete(repo.getBackfillComplete());
        return response;
    }

    @Override
    public void resetSync(String owner, String name, Instant since, boolean purgeExisting) throws RepoNotFoundException {
        Repo repo = repoRepository.findByOwnerAndName(owner, name)
                .orElseThrow(() -> new RepoNotFoundException(
                        String.format("repository %s/%s not found", owner, name)));

        if (purgeExisting) {
            long deleted = commitRepository.deleteByRepoId(repo.getId());
            log.info("purged {} commit(s) for {}/{} as part of reset", deleted, owner, name);
        }
        repo.setSyncSince(since);
        repo.setSyncCursorPage(1);
        repo.setBackfillComplete(false);
        repoRepository.save(repo);
    }

    @Override
    public void syncAllRepos() {
        List<Repo> repos = repoRepository.findAll();
        for (Repo repo : repos) {
            try {
                syncRepo(repo);
            } catch (Exception e) {
                log.error("scheduled sync failed for {}/{}", repo.getOwner(), repo.getName(), e);
            }
        }
    }

    private void refreshMetadata(Repo repo) {
        try {
            applyMetadata(repo, gitHubApiService.fetchRepo(repo.getOwner(), repo.getName()));
        } catch (GitHubApiException e) {
            log.warn("could not refresh metadata for {}/{}: {}", repo.getOwner(), repo.getName(), e.getMessage());
        }
    }

    private void applyMetadata(Repo repo, GitHubRepoResponse remote) {
        repo.setDescription(remote.getDescription());
        repo.setHtmlUrl(remote.getHtmlUrl());
        repo.setLanguage(remote.getLanguage());
        repo.setForksCount(remote.getForksCount());
        repo.setStarsCount(remote.getStarsCount());
        repo.setOpenIssuesCount(remote.getOpenIssuesCount());
        repo.setWatchersCount(remote.getWatchersCount());
        repo.setGithubCreatedAt(remote.getGithubCreatedAt());
        repo.setGithubUpdatedAt(remote.getGithubUpdatedAt());
    }

    private Commit toEntity(GitHubCommitResponse remote, Repo repo) {
        Commit commit = new Commit();
        commit.setSha(remote.getSha());
        commit.setHtmlUrl(remote.getHtmlUrl());
        commit.setRepo(repo);
        if (remote.getCommit() != null) {
            commit.setMessage(remote.getCommit().getMessage());
            if (remote.getCommit().getAuthor() != null) {
                commit.setAuthorName(remote.getCommit().getAuthor().getName());
                commit.setAuthorEmail(remote.getCommit().getAuthor().getEmail());
                commit.setCommittedAt(remote.getCommit().getAuthor().getDate());
            }
        }
        return commit;
    }
}
