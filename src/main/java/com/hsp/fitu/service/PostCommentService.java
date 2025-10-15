package com.hsp.fitu.service;

import com.hsp.fitu.dto.PostCommentCreateRequestDTO;
import com.hsp.fitu.dto.PostCommentResponseDTO;
import com.hsp.fitu.dto.PostCommentUpdateRequestDTO;

public interface PostCommentService {
    PostCommentResponseDTO createComment(Long postId, PostCommentCreateRequestDTO commentCreateRequestDTO, Long writerId);
    PostCommentResponseDTO updateComment(Long postId, Long commentId, PostCommentUpdateRequestDTO commentUpdateRequestDTO);
    void deleteComment(Long postId, Long commentId);
}
