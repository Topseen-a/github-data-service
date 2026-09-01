package com.githubservice.githubdataservice.startup;

import com.githubservice.githubdataservice.config.GitHubRepoConfig;
import com.githubservice.githubdataservice.service.SyncService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
@Slf4j
@ConditionalOnProperty(prefix = "github.sync", name = "seed-enabled", havingValue = "true", matchIfMissing = true)
public class RepoSeeder implements ApplicationRunner {
    private final SyncService syncService;
    private final GitHubRepoConfig gitHubRepoConfig;

    @Override
    public void run(ApplicationArguments args) {
        String owner = gitHubRepoConfig.getOwner();
        String name = gitHubRepoConfig.getName();
        if (owner == null || owner.isBlank() || name == null || name.isBlank()) {
            log.info("no default repository configured, skipping seed");
            return;
        }
        try {
            syncService.ensureRepo(owner, name);
        } catch (Exception e) {
            log.error("failed to register configured repo {}/{} on startup", owner, name, e);
        }
    }
}
