package com.hsp.fitu.service;

import com.hsp.fitu.dto.PostCreateRequestDTO;
import com.hsp.fitu.dto.PostResponseDTO;
import com.hsp.fitu.dto.PostSliceResponseDTO;
import com.hsp.fitu.dto.PostUpdateRequestDTO;
import com.hsp.fitu.entity.enums.PostCategory;

import java.util.List;

public interface PostService {
    PostResponseDTO createPost(long writerId, long universityId, PostCreateRequestDTO requestDTO);
    PostSliceResponseDTO<PostResponseDTO> getAllPosts(PostCategory category, int page, int size);
    PostResponseDTO getPost(long postId);
    PostSliceResponseDTO<PostResponseDTO> searchPosts(PostCategory category, String keyword, int page, int size);
    PostResponseDTO updatePost(long postId, PostUpdateRequestDTO postUpdateRequestDTO);
    void deletePost(long postId);
}
