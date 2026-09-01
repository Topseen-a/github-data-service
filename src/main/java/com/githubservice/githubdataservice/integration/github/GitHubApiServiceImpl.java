package com.githubservice.githubdataservice.integration.github;

import com.githubservice.githubdataservice.config.GitHubApiConfig;
import com.githubservice.githubdataservice.dto.response.GitHubCommitResponse;
import com.githubservice.githubdataservice.dto.response.GitHubRepoResponse;
import com.githubservice.githubdataservice.exception.GitHubApiException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.time.Instant;
import java.util.List;

@Service
@Slf4j
@AllArgsConstructor
public class GitHubApiServiceImpl implements GitHubApiService {
    private final RestTemplate restTemplate;
    private final GitHubApiConfig gitHubApiConfig;

    @Override
    public GitHubRepoResponse fetchRepo(String owner, String name) {
        try {
            URI uri = URI.create(gitHubApiConfig.getBaseUrl() + "/repos/" + owner + "/" + name);
            RequestEntity<Void> request = RequestEntity.get(uri).headers(buildHeaders()).build();
            return restTemplate.exchange(request, GitHubRepoResponse.class).getBody();
        } catch (RestClientException e) {
            throw new GitHubApiException("failed to fetch repository " + owner + "/" + name + " from GitHub", e);
        }
    }

    @Override
    public GitHubCommitPage fetchCommits(String owner, String name, Instant since, int page, int perPage) {
        try {
            StringBuilder url = new StringBuilder(gitHubApiConfig.getBaseUrl())
                    .append("/repos/").append(owner).append("/").append(name).append("/commits")
                    .append("?page=").append(page)
                    .append("&per_page=").append(perPage);
            if (since != null) {
                url.append("&since=").append(since);
            }
            URI uri = URI.create(url.toString());
            RequestEntity<Void> request = RequestEntity.get(uri).headers(buildHeaders()).build();
            ResponseEntity<GitHubCommitResponse[]> response = restTemplate.exchange(request, GitHubCommitResponse[].class);
            List<GitHubCommitResponse> commits = response.getBody() == null ? List.of() : List.of(response.getBody());
            boolean hasNext = hasNextPage(response.getHeaders().getFirst(HttpHeaders.LINK));
            return new GitHubCommitPage(commits, hasNext);
        } catch (RestClientException e) {
            throw new GitHubApiException("failed to fetch commits for " + owner + "/" + name + " (page " + page + ")", e);
        }
    }

    private HttpHeaders buildHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept", "application/vnd.github+json");
        headers.set("X-GitHub-Api-Version", "2022-11-28");
        if (gitHubApiConfig.getToken() != null && !gitHubApiConfig.getToken().isBlank()) {
            headers.setBearerAuth(gitHubApiConfig.getToken());
        }
        return headers;
    }

    private boolean hasNextPage(String linkHeader) {
        if (linkHeader == null || linkHeader.isBlank()) {
            return false;
        }
        for (String part : linkHeader.split(",")) {
            if (part.contains("rel=\"next\"")) {
                return true;
            }
        }
        return false;
    }
}
