package com.hsp.fitu.service;


import com.hsp.fitu.dto.*;
import com.hsp.fitu.entity.PostEntity;
import com.hsp.fitu.entity.enums.PostCategory;
import com.hsp.fitu.mapper.PostMapper;
import com.hsp.fitu.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {
    private final PostRepository postRepository;
    private final PostMapper postMapper;

    @Override
    @Transactional
    public PostResponseDTO createPost(long writerId, long universityId, PostCreateRequestDTO requestDTO) {

        PostEntity postEntity = PostEntity.builder()
                .category(requestDTO.category())
                .title(requestDTO.title())
                .contents(requestDTO.contents())
                .universityId(universityId)
                .writerId(writerId)
                .build();

        PostEntity saved = postRepository.save(postEntity);
        return postMapper.postToDTO(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public PostSliceResponseDTO<PostListResponseDTO> getAllPosts(PostCategory category, Long universityId, int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"));

        Slice<PostListResponseDTO> slice = postRepository.findAllWithUniversityName(category, universityId, pageRequest);

        return new PostSliceResponseDTO<>(slice.getContent(), slice.hasNext());
    }

    @Override
    @Transactional
    public PostResponseDTO getPost(long postId) {
        PostEntity postEntity = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));

        return postMapper.postToDTO(postEntity);
    }

    @Override
    @Transactional
    public PostSliceResponseDTO<PostListResponseDTO> searchPosts(Long universityId, PostCategory category, String keyword, int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"));

        Slice<PostListResponseDTO> slice = postRepository.searchByUniversityAndKeyword(universityId, category, keyword, pageRequest);
        List<PostListResponseDTO> content = slice.getContent();

        return new PostSliceResponseDTO<>(content, slice.hasNext());
    }

    @Override
    @Transactional
    public PostResponseDTO updatePost(long postId, PostUpdateRequestDTO postUpdateRequestDTO) {
        PostEntity postEntity = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));
        postEntity.update(
                postUpdateRequestDTO.title(),
                postUpdateRequestDTO.contents()
        );
        return postMapper.postToDTO(postEntity);
    }

    @Override
    public void deletePost(long postId) {
        postRepository.deleteById(postId);
    }

}