package com.githubservice.githubdataservice.scheduler;

import com.githubservice.githubdataservice.service.SyncService;
import lombok.AllArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
@ConditionalOnProperty(prefix = "github.sync", name = "enabled", havingValue = "true", matchIfMissing = true)
public class CommitSyncScheduler {
    private final SyncService syncService;

    @Scheduled(initialDelayString = "${github.sync.initial-delay-ms}", fixedDelayString = "${github.sync.interval-ms}")
    public void syncAll() {
        syncService.syncAllRepos();
    }
}
