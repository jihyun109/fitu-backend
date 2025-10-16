package com.hsp.fitu.service;

import com.hsp.fitu.dto.*;
import com.hsp.fitu.entity.enums.PostCategory;

import java.util.List;

public interface PostService {
    PostResponseDTO createPost(long writerId, long universityId, PostCreateRequestDTO requestDTO);
    PostSliceResponseDTO<PostListResponseDTO> getAllPosts(PostCategory category, Long universityId, int page, int size);
    PostResponseDTO getPost(long postId);
    PostSliceResponseDTO<PostListResponseDTO> searchPosts(Long universityId, PostCategory category, String keyword, int page, int size);
    PostResponseDTO updatePost(long postId, PostUpdateRequestDTO postUpdateRequestDTO);
    void deletePost(long postId);
}
