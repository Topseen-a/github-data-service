package com.githubservice.githubdataservice.repository;

import com.githubservice.githubdataservice.model.Commit;
import com.githubservice.githubdataservice.model.Repo;
import com.githubservice.githubdataservice.repository.projection.AuthorCommitCount;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class CommitRepositoryTest {
    @Autowired
    private RepoRepository repoRepository;
    @Autowired
    private CommitRepository commitRepository;

    @Test
    void findTopAuthorsByRepoTest() {
        Repo repo = new Repo();
        repo.setOwner("chromium");
        repo.setName("chromium");
        repo.setSyncSince(Instant.parse("2026-01-01T00:00:00Z"));
        repo.setSyncCursorPage(1);
        repo.setBackfillComplete(false);
        repo = repoRepository.save(repo);

        saveCommit(repo, "sha1", "Alice", Instant.parse("2026-01-01T00:00:00Z"));
        saveCommit(repo, "sha2", "Alice", Instant.parse("2026-01-02T00:00:00Z"));
        saveCommit(repo, "sha3", "Bob", Instant.parse("2026-01-03T00:00:00Z"));

        List<AuthorCommitCount> topAuthors = commitRepository.findTopAuthorsByRepo(
                "chromium", "chromium", PageRequest.of(0, 10));

        assertThat(topAuthors).hasSize(2);
        assertThat(topAuthors.get(0).getAuthorName()).isEqualTo("Alice");
        assertThat(topAuthors.get(0).getCommitCount()).isEqualTo(2L);
    }

    @Test
    void existsByRepoIdAndShaTest() {
        Repo repo = new Repo();
        repo.setOwner("torvalds");
        repo.setName("linux");
        repo.setSyncSince(Instant.parse("2026-01-01T00:00:00Z"));
        repo.setSyncCursorPage(1);
        repo.setBackfillComplete(false);
        repo = repoRepository.save(repo);
        saveCommit(repo, "dup-sha", "Carol", Instant.now());

        assertThat(commitRepository.existsByRepoIdAndSha(repo.getId(), "dup-sha")).isTrue();
        assertThat(commitRepository.existsByRepoIdAndSha(repo.getId(), "missing-sha")).isFalse();
    }

    private void saveCommit(Repo repo, String sha, String authorName, Instant committedAt) {
        Commit commit = new Commit();
        commit.setSha(sha);
        commit.setMessage("message for " + sha);
        commit.setAuthorName(authorName);
        commit.setAuthorEmail(sha + "@example.com");
        commit.setCommittedAt(committedAt);
        commit.setHtmlUrl("https://github.com/" + repo.getOwner() + "/" + repo.getName() + "/commit/" + sha);
        commit.setRepo(repo);
        commitRepository.save(commit);
    }
}
