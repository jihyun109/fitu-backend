package com.hsp.fitu.service;

import com.hsp.fitu.dto.*;
import com.hsp.fitu.entity.PostEntity;
import com.hsp.fitu.entity.enums.PostCategory;
import com.hsp.fitu.mapper.PostMapper;
import com.hsp.fitu.repository.PostRepository;
import com.hsp.fitu.repository.UniversityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {
    private final PostRepository postRepository;
    private final PostMapper postMapper;
    private final UniversityRepository universityRepository;

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

        String universityName = universityRepository.findUniversityNameById(universityId);

        Slice<PostListResponseDTO> posts = postRepository.findPostsByUniversityAndCategory(universityId, category, pageRequest);

        return new PostSliceResponseDTO<>(universityName, posts.getContent(), posts.hasNext());
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

        String universityName = universityRepository.findUniversityNameById(universityId);

        Slice<PostListResponseDTO> posts = postRepository.searchPostsByUniversityAndKeyword(universityId, category, keyword, pageRequest);

        return new PostSliceResponseDTO<>(universityName, posts.getContent(), posts.hasNext());
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