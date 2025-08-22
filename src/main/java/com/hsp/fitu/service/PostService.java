package com.hsp.fitu.service;

import com.hsp.fitu.dto.PostCreateRequestDTO;
import com.hsp.fitu.dto.PostResponseDTO;
import com.hsp.fitu.dto.PostUpdateRequestDTO;

import java.util.List;
import java.util.Optional;

public interface PostService {
    List<PostResponseDTO> getAllPosts();
    Optional<PostResponseDTO> getPostById(Long id);
    PostResponseDTO createPost(PostCreateRequestDTO postCreateRequestDTO);
    PostResponseDTO updatePost(Long id, PostUpdateRequestDTO postUpdateRequestDTO);
    void deletePost(Long id);
}
