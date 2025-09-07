package com.hsp.fitu.mapper;

import com.hsp.fitu.dto.PostCommentResponseDTO;
import com.hsp.fitu.entity.PostCommentEntity;


public class PostCommentMapper {
    public static PostCommentResponseDTO commentToDTO(PostCommentEntity postCommentEntity) {
        return PostCommentResponseDTO.builder()
                .id(postCommentEntity.getId())
                .postId(postCommentEntity.getPostId())
                .writerId(postCommentEntity.getWriterId())
                .rootId(postCommentEntity.getRootId())
                .contents(postCommentEntity.getContents())
                .createdAt(postCommentEntity.getCreatedAt())
                .build();
    }
}