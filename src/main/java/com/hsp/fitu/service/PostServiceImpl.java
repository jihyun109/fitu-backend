package com.hsp.fitu.service;


import com.hsp.fitu.dto.PostCreateRequestDTO;
import com.hsp.fitu.dto.PostResponseDTO;
import com.hsp.fitu.dto.PostUpdateRequestDTO;
import com.hsp.fitu.entity.PostEntity;
import com.hsp.fitu.mapper.PostMapper;
import com.hsp.fitu.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {
    private final PostRepository postRepository;

    @Override
    public PostResponseDTO createPost(PostCreateRequestDTO requestDTO) {
        PostEntity post = PostEntity.builder()
                .universityId(requestDTO.getUniversityId())
                .writerId(requestDTO.getWriterId())
                .category(requestDTO.getCategory())
                .title(requestDTO.getTitle())
                .contents(requestDTO.getContents())
                .build();

        PostEntity saved  = postRepository.save(post);
        return PostMapper.postToDTO(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PostResponseDTO> getAllPosts() {
        return postRepository.findAll()
                .stream()
                .map(PostMapper::postToDTO)
                .toList();
    }

    @Override
    @Transactional
    public PostResponseDTO getPost(Long id) {
        PostEntity postEntity = postRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));

        PostEntity updatedPost = postRepository.save(postEntity);
        return PostMapper.postToDTO(updatedPost);
    }

    @Override
    @Transactional
    public PostResponseDTO updatePost(Long id, PostUpdateRequestDTO postUpdateRequestDTO) {
        PostEntity postEntity = postRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));
        postEntity.update(
                postUpdateRequestDTO.getTitle(),
                postUpdateRequestDTO.getContents()
        );
        return PostMapper.postToDTO(postEntity);
    }

    @Override
    public void deletePost(Long id) {
        postRepository.deleteById(id);
    }

}
