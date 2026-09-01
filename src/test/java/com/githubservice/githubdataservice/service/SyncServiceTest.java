package com.githubservice.githubdataservice.service;

import com.githubservice.githubdataservice.config.GitHubRepoConfig;
import com.githubservice.githubdataservice.config.GitHubSyncConfig;
import com.githubservice.githubdataservice.dto.response.GitHubCommitAuthor;
import com.githubservice.githubdataservice.dto.response.GitHubCommitDetails;
import com.githubservice.githubdataservice.dto.response.GitHubCommitResponse;
import com.githubservice.githubdataservice.dto.response.GitHubRepoResponse;
import com.githubservice.githubdataservice.dto.response.SyncResultResponse;
import com.githubservice.githubdataservice.integration.github.GitHubApiService;
import com.githubservice.githubdataservice.integration.github.GitHubCommitPage;
import com.githubservice.githubdataservice.model.Repo;
import com.githubservice.githubdataservice.repository.CommitRepository;
import com.githubservice.githubdataservice.repository.RepoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class SyncServiceTest {
    @Mock
    private GitHubApiService gitHubApiService;
    @Mock
    private RepoRepository repoRepository;
    @Mock
    private CommitRepository commitRepository;
    @Mock
    private GitHubRepoConfig gitHubRepoConfig;
    @Mock
    private GitHubSyncConfig gitHubSyncConfig;
    @InjectMocks
    private SyncServiceImpl syncService;

    @Test
    public void testSyncRepoSavesOnlyCommitsNotAlreadyPersisted() {
        Repo repo = new Repo();
        repo.setId("repo-1");
        repo.setOwner("chromium");
        repo.setName("chromium");
        repo.setSyncSince(Instant.parse("2026-01-01T00:00:00Z"));
        repo.setSyncCursorPage(1);
        repo.setBackfillComplete(false);

        GitHubCommitResponse alreadyStored = buildCommitResponse("sha-existing", "2026-01-02T10:00:00Z");
        GitHubCommitResponse notYetStored = buildCommitResponse("sha-new", "2026-01-02T11:00:00Z");

        when(gitHubSyncConfig.getPerPage()).thenReturn(100);
        when(gitHubSyncConfig.getMaxPagesPerRun()).thenReturn(5);
        when(gitHubApiService.fetchRepo("chromium", "chromium")).thenReturn(buildRepoResponse());
        when(gitHubApiService.fetchCommits(eq("chromium"), eq("chromium"), any(), eq(1), eq(100)))
                .thenReturn(new GitHubCommitPage(List.of(alreadyStored, notYetStored), false));
        when(commitRepository.existsByRepoIdAndSha("repo-1", "sha-existing")).thenReturn(true);
        when(commitRepository.existsByRepoIdAndSha("repo-1", "sha-new")).thenReturn(false);

        SyncResultResponse result = syncService.syncRepo(repo);

        assertThat(result.getCommitsSaved()).isEqualTo(1);
        assertThat(result.getPagesFetched()).isEqualTo(1);
        assertThat(result.isBackfillComplete()).isTrue();
        verify(commitRepository, times(1)).save(argThat(commit -> "sha-new".equals(commit.getSha())));
        verify(commitRepository, never()).save(argThat(commit -> "sha-existing".equals(commit.getSha())));
        assertThat(repo.getSyncSince()).isEqualTo(Instant.parse("2026-01-02T11:00:00Z"));
    }

    @Test
    public void testSyncRepoStopsPagingWhenGitHubSignalsNoNextPage() {
        Repo repo = new Repo();
        repo.setId("repo-2");
        repo.setOwner("chromium");
        repo.setName("chromium");
        repo.setSyncSince(Instant.parse("2026-01-01T00:00:00Z"));
        repo.setSyncCursorPage(1);
        repo.setBackfillComplete(false);

        when(gitHubSyncConfig.getPerPage()).thenReturn(100);
        when(gitHubSyncConfig.getMaxPagesPerRun()).thenReturn(5);
        when(gitHubApiService.fetchRepo("chromium", "chromium")).thenReturn(buildRepoResponse());
        when(gitHubApiService.fetchCommits(eq("chromium"), eq("chromium"), any(), eq(1), eq(100)))
                .thenReturn(new GitHubCommitPage(List.of(), false));

        SyncResultResponse result = syncService.syncRepo(repo);

        assertThat(result.getCommitsSaved()).isZero();
        assertThat(result.getPagesFetched()).isEqualTo(1);
        assertThat(result.isBackfillComplete()).isTrue();
        verify(commitRepository, never()).save(any());
    }

    private GitHubCommitResponse buildCommitResponse(String sha, String date) {
        GitHubCommitAuthor author = new GitHubCommitAuthor();
        author.setName("Jane Doe");
        author.setEmail("jane@example.com");
        author.setDate(Instant.parse(date));

        GitHubCommitDetails details = new GitHubCommitDetails();
        details.setMessage("test commit " + sha);
        details.setAuthor(author);

        GitHubCommitResponse response = new GitHubCommitResponse();
        response.setSha(sha);
        response.setCommit(details);
        response.setHtmlUrl("https://github.com/chromium/chromium/commit/" + sha);
        return response;
    }

    private GitHubRepoResponse buildRepoResponse() {
        GitHubRepoResponse response = new GitHubRepoResponse();
        response.setName("chromium");
        response.setDescription("The Chromium projects");
        response.setHtmlUrl("https://github.com/chromium/chromium");
        response.setStarsCount(20000);
        return response;
    }
}
