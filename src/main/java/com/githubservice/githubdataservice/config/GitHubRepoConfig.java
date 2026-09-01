package com.githubservice.githubdataservice.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.time.Instant;

@Configuration
@ConfigurationProperties(prefix = "github.repository")
@Getter
@Setter
public class GitHubRepoConfig {
    private String owner;
    private String name;
    private Instant startDate;
}
