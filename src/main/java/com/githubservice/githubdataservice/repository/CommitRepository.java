package com.githubservice.githubdataservice.repository;

import com.githubservice.githubdataservice.model.Commit;
import com.githubservice.githubdataservice.repository.projection.AuthorCommitCount;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CommitRepository extends JpaRepository<Commit, String> {
    boolean existsByRepoIdAndSha(String repoId, String sha);

    Page<Commit> findByRepo_OwnerAndRepo_NameOrderByCommittedAtDesc(String owner, String name, Pageable pageable);

    long deleteByRepoId(String repoId);

    @Query("""
            SELECT c.authorName AS authorName, COUNT(c) AS commitCount
            FROM Commit c
            WHERE c.repo.owner = :owner AND c.repo.name = :name
            GROUP BY c.authorName
            ORDER BY COUNT(c) DESC
            """)
    List<AuthorCommitCount> findTopAuthorsByRepo(@Param("owner") String owner, @Param("name") String name, Pageable pageable);
}
