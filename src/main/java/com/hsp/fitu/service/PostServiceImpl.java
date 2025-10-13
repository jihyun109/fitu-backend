package com.hsp.fitu.service;


import com.hsp.fitu.dto.PostCreateRequestDTO;
import com.hsp.fitu.dto.PostResponseDTO;
import com.hsp.fitu.dto.PostSliceResponseDTO;
import com.hsp.fitu.dto.PostUpdateRequestDTO;
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
    public PostSliceResponseDTO<PostResponseDTO> getAllPosts(PostCategory category, int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"));
        Slice<PostEntity> slice = postRepository.findAllByCategoryOrderByIdDesc(category, pageRequest);

        List<PostResponseDTO> content = slice.getContent().stream().map(postMapper::postToDTO).toList();

        return new PostSliceResponseDTO<>(content, slice.hasNext());
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
    public PostSliceResponseDTO<PostResponseDTO> searchPosts(PostCategory category, String keyword, int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"));

        Slice<PostEntity> slice = postRepository
                .findByCategoryAndTitleContainingIgnoreCaseOrCategoryAndContentsContainingIgnoreCase(
                        category, keyword,
                        category, keyword,
                        pageRequest
                );
        List<PostResponseDTO> content = slice.getContent().stream().map(postMapper::postToDTO).toList();

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