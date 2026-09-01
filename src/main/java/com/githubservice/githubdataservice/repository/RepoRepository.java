package com.githubservice.githubdataservice.repository;

import com.githubservice.githubdataservice.model.Repo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RepoRepository extends JpaRepository<Repo, String> {
    Optional<Repo> findByOwnerAndName(String owner, String name);
}
