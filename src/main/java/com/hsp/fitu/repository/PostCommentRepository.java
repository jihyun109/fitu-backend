package com.hsp.fitu.repository;

import com.hsp.fitu.entity.PostCommentsEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostCommentRepository extends JpaRepository<PostCommentsEntity, Long> {
    List<PostCommentsEntity> findAllByPostIdOrderByRootIdAscIdAsc(Long postId);
    void deleteAllByPostId(Long postId);
}
