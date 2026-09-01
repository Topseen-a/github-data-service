package com.githubservice.githubdataservice.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "github.sync")
@Getter
@Setter
public class GitHubSyncConfig {
    private int perPage;
    private int maxPagesPerRun;
    private long intervalMs;
    private long initialDelayMs;
    private boolean enabled;
    private boolean seedEnabled;
}
