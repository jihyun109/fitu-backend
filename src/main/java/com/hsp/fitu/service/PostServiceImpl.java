package com.hsp.fitu.service;


import com.hsp.fitu.dto.PostCreateRequestDTO;
import com.hsp.fitu.dto.PostResponseDTO;
import com.hsp.fitu.dto.PostUpdateRequestDTO;
import com.hsp.fitu.entity.PostEntity;
import com.hsp.fitu.repository.PostRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {
    private final PostRepository postRepository;

    private PostResponseDTO convertToDto(PostEntity post) {
        if(post == null) {
            return null;
        }
        return new PostResponseDTO(
                post.getId(),
                post.getCategory(),
                post.getUniversityId(),
                post.getWriterId(),
                post.getTitle(),
                post.getContent(),
                post.getCreatedAt()
        );
    }

    @Transactional
    @Override //게시글 조회
    public List<PostResponseDTO> getAllPosts() {
        return postRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    // ID로 게시글 조회
    @Transactional
    @Override
    public Optional<PostResponseDTO> getPostById(Long id) {
        Optional<PostEntity> postOptional = postRepository.findById(id);
        return postOptional.map(this::convertToDto);
    }

    @Override //게시글 생성
    public PostResponseDTO createPost(PostCreateRequestDTO postCreateRequestDTO) {
        PostEntity post = PostEntity.builder()
                .category(postCreateRequestDTO.getCategory())
                .universityId(postCreateRequestDTO.getUniversityId())
                .writerId(postCreateRequestDTO.getWriterId())
                .title(postCreateRequestDTO.getTitle())
                .content(postCreateRequestDTO.getContent())
                .build();

        PostEntity savedPost = postRepository.save(post);
        return convertToDto(savedPost);
    }

    @Override //게시글 수정
    public PostResponseDTO updatePost(Long id, PostUpdateRequestDTO postUpdateRequestDTO) {
        return postRepository.findById(id).map(post -> {
            post.update(
                    postUpdateRequestDTO.getCategory(),
                    postUpdateRequestDTO.getUniversityId(),
                    postUpdateRequestDTO.getTitle(),
                    postUpdateRequestDTO.getContent()
            );
            PostEntity savedPost = postRepository.save(post);
            return convertToDto(savedPost);
        }).orElse(null);
    }

    @Override //게시글 삭제
    public void deletePost(Long id) {
        postRepository.deleteById(id);
    }
    
}
