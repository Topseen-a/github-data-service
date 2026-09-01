package com.githubservice.githubdataservice.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;

@Entity
@Getter
@Setter
@Table(
        name = "commits",
        uniqueConstraints = @UniqueConstraint(columnNames = {"repo_id", "sha"}),
        indexes = {
                @Index(name = "idx_commits_repo_committed_at", columnList = "repo_id, committedAt"),
                @Index(name = "idx_commits_author_name", columnList = "authorName")
        }
)
public class Commit {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    private String sha;
    @Column(columnDefinition = "TEXT")
    private String message;
    private String authorName;
    private String authorEmail;
    private Instant committedAt;
    private String htmlUrl;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "repo_id")
    private Repo repo;
    @CreationTimestamp
    private Instant createdAt;
}
