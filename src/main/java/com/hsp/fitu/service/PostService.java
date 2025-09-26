package com.hsp.fitu.service;

import com.hsp.fitu.dto.PostCreateRequestDTO;
import com.hsp.fitu.dto.PostResponseDTO;
import com.hsp.fitu.dto.PostUpdateRequestDTO;

import java.util.List;

public interface PostService {
    PostResponseDTO createPost(Long writerId, Long universityId, PostCreateRequestDTO requestDTO);
    List<PostResponseDTO> getAllPosts();
    PostResponseDTO getPost(Long postId);
    PostResponseDTO updatePost(Long postId, PostUpdateRequestDTO postUpdateRequestDTO);
    void deletePost(Long postId);
}
