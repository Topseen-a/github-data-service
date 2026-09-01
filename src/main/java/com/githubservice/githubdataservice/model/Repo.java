package com.githubservice.githubdataservice.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;

@Entity
@Getter
@Setter
@Table(name = "repos", uniqueConstraints = @UniqueConstraint(columnNames = {"owner", "name"}))
public class Repo {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    private String owner;
    private String name;
    private String description;
    private String htmlUrl;
    private String language;
    private Integer forksCount;
    private Integer starsCount;
    private Integer openIssuesCount;
    private Integer watchersCount;
    private Instant githubCreatedAt;
    private Instant githubUpdatedAt;
    private Instant syncSince;
    private Integer syncCursorPage;
    private Boolean backfillComplete;
    private Instant lastSyncedAt;
    @CreationTimestamp
    private Instant createdAt;
    @UpdateTimestamp
    private Instant updatedAt;
}
