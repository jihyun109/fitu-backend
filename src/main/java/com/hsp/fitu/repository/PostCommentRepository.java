package com.hsp.fitu.repository;

import com.hsp.fitu.dto.PostCommentResponseDTO;
import com.hsp.fitu.entity.PostCommentsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PostCommentRepository extends JpaRepository<PostCommentsEntity, Long> {
    @Query("""
    SELECT new com.hsp.fitu.dto.PostCommentResponseDTO(
        c.id,
        u.name,
        m.url,
        c.rootId,
        c.contents,
        c.createdAt,
        true,
        c.isSecret
    )
    FROM PostCommentsEntity c
    JOIN UserEntity u ON c.writerId = u.id
    LEFT JOIN MediaFilesEntity m ON u.profileImgId = m.id
    WHERE c.id = :commentId
""")
    PostCommentResponseDTO findCommentDTOById(@Param("commentId") Long commentId);

    @Query("""
    SELECT new com.hsp.fitu.dto.PostCommentResponseDTO(
        c.id,
        u.name,
        m.url,
        c.rootId,
        c.contents,
        c.createdAt,
        false,
        c.isSecret
    )
    FROM PostCommentsEntity c
    JOIN UserEntity u ON c.writerId = u.id
    LEFT JOIN MediaFilesEntity m ON u.profileImgId = m.id
    WHERE c.postId = :postId
    ORDER BY c.createdAt ASC
    """)
    List<PostCommentResponseDTO> findCommentsByPostId(@Param("postId") long postId);

    void deleteByPostId(long postId);
}
