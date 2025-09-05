package com.hsp.fitu.service;

import com.hsp.fitu.dto.PostCreateRequestDTO;
import com.hsp.fitu.dto.PostResponseDTO;
import com.hsp.fitu.dto.PostUpdateRequestDTO;

import java.util.List;

public interface PostService {
    PostResponseDTO createPost(PostCreateRequestDTO requestDTO);
    List<PostResponseDTO> getAllPosts();
    PostResponseDTO getPost(Long id);
    PostResponseDTO updatePost(Long id, PostUpdateRequestDTO postUpdateRequestDTO);
    void deletePost(Long id);
}
