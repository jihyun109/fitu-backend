package com.hsp.fitu.service;

import com.hsp.fitu.dto.PostCommentCreateRequestDTO;
import com.hsp.fitu.dto.PostCommentResponseDTO;
import com.hsp.fitu.dto.PostCommentUpdateRequestDTO;

import java.util.List;

public interface PostCommentService {
    PostCommentResponseDTO createComment(PostCommentCreateRequestDTO commentCreateRequestDTO);
    List<PostCommentResponseDTO> getCommentsByPost(Long postId);
    PostCommentResponseDTO updateComment(Long id, PostCommentUpdateRequestDTO commentUpdateRequestDTO);
    void deleteComment(Long id);
}
