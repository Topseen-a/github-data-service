package com.githubservice.githubdataservice.controller;

import com.githubservice.githubdataservice.dto.request.ResetSyncRequest;
import com.githubservice.githubdataservice.dto.response.SyncResultResponse;
import com.githubservice.githubdataservice.exception.RepoNotFoundException;
import com.githubservice.githubdataservice.service.SyncService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/repos/{owner}/{name}")
@AllArgsConstructor
public class SyncController {
    private final SyncService syncService;

    @PostMapping("/sync")
    @ResponseStatus(HttpStatus.OK)
    public SyncResultResponse sync(@PathVariable String owner, @PathVariable String name) {
        return syncService.triggerSync(owner, name);
    }

    @PostMapping("/reset")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void reset(
            @PathVariable String owner,
            @PathVariable String name,
            @Valid @RequestBody ResetSyncRequest request) throws RepoNotFoundException {
        syncService.resetSync(owner, name, request.getSince(), request.isPurgeExisting());
    }
}
