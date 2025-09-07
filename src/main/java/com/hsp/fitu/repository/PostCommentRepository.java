package com.hsp.fitu.repository;

import com.hsp.fitu.entity.PostCommentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostCommentRepository extends JpaRepository<PostCommentEntity, Long> {
    List<PostCommentEntity> findByPostIdAndParentIsNull(Long postId);
}
