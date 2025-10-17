package com.hsp.fitu.repository;

import com.hsp.fitu.dto.PostListResponseDTO;
import com.hsp.fitu.dto.PostResponseDTO;
import com.hsp.fitu.entity.PostEntity;
import com.hsp.fitu.entity.enums.PostCategory;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PostRepository extends JpaRepository<PostEntity, Long> {
    @Query("""
    SELECT new com.hsp.fitu.dto.PostListResponseDTO(
        p.id,
        w.name,
        p.category,
        p.title,
        p.contents,
        p.createdAt
    )
    FROM PostEntity p
    JOIN UserEntity w ON p.writerId = w.id
    WHERE p.universityId = :universityId
      AND p.category = :category
    ORDER BY p.id DESC
    """)
    Slice<PostListResponseDTO> findPostsByUniversityAndCategory(
            @Param("universityId") Long universityId,
            @Param("category") PostCategory category,
            Pageable pageable
    );

    @Query("""
    SELECT new com.hsp.fitu.dto.PostResponseDTO(
        p.id,
        p.category,
        p.title,
        u.name,
        m.url,
        p.contents,
        p.createdAt
    )
    FROM PostEntity p
    JOIN UserEntity u ON p.writerId = u.id
    LEFT JOIN MediaFilesEntity m ON u.profileImgId = m.id
    WHERE p.id = :postId
    """)
    Optional<PostResponseDTO> findPostWithWriter(@Param("postId") long postId);

    @Query("""
        SELECT new com.hsp.fitu.dto.PostListResponseDTO(
            p.id,
            w.name,
            p.category,
            p.title,
            p.contents,
            p.createdAt
        )
        FROM PostEntity p
        JOIN UserEntity w ON p.writerId = w.id
        WHERE p.universityId = :universityId
          AND p.category = :category
          AND (LOWER(p.title) LIKE LOWER(CONCAT('%', :keyword, '%'))
               OR LOWER(p.contents) LIKE LOWER(CONCAT('%', :keyword, '%')))
        ORDER BY p.id DESC
    """)
    Slice<PostListResponseDTO> searchPostsByUniversityAndKeyword(
            @Param("universityId") Long universityId,
            @Param("category") PostCategory category,
            @Param("keyword") String keyword,
            Pageable pageable
    );
}
