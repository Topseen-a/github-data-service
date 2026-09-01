package com.githubservice.githubdataservice.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
public class ResetSyncRequest {
    @NotNull(message = "since is required")
    private Instant since;

    private boolean purgeExisting;
}
