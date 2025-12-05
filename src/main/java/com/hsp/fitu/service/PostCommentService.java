package com.hsp.fitu.service;

import com.hsp.fitu.dto.PostCommentCreateRequestDTO;
import com.hsp.fitu.dto.PostCommentResponseDTO;

import java.util.List;

public interface PostCommentService {
    PostCommentResponseDTO createComment(Long postId, PostCommentCreateRequestDTO commentCreateRequestDTO, Long writerId);
    List<PostCommentResponseDTO> getComments(Long postId, Long currentUserId, Long postWriterId);
    void deleteComment(Long postId, Long commentId, Long writerId);
}
