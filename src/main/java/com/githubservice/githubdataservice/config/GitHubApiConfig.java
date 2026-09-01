package com.githubservice.githubdataservice.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "github.api")
@Getter
@Setter
public class GitHubApiConfig {
    private String baseUrl;
    private String token;
}
