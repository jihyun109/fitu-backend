package com.hsp.fitu.service;


import com.hsp.fitu.dto.PostCommentResponseDTO;
import com.hsp.fitu.dto.PostCreateRequestDTO;
import com.hsp.fitu.dto.PostResponseDTO;
import com.hsp.fitu.dto.PostUpdateRequestDTO;
import com.hsp.fitu.entity.PostCommentEntity;
import com.hsp.fitu.entity.PostEntity;
import com.hsp.fitu.entity.UserEntity;
import com.hsp.fitu.mapper.PostCommentMapper;
import com.hsp.fitu.mapper.PostMapper;
import com.hsp.fitu.repository.PostCommentRepository;
import com.hsp.fitu.repository.PostRepository;
import com.hsp.fitu.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {
    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final PostMapper postMapper;
    private final PostCommentRepository postCommentRepository;
    private final PostCommentMapper postCommentMapper;

    @Override
    @Transactional
    public PostResponseDTO createPost(Long writerId, Long universityId, PostCreateRequestDTO requestDTO) {
        UserEntity writer = userRepository.findById(writerId)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 사용자입니다."));

        PostEntity postEntity = PostEntity.builder()
                .category(requestDTO.category())
                .title(requestDTO.title())
                .contents(requestDTO.contents())
                .universityId(universityId)
                .writerId(writer)
                .build();

        PostEntity saved = postRepository.save(postEntity);
        return postMapper.postToDTO(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PostResponseDTO> getAllPosts() {
        return postRepository.findAll()
                .stream()
                .map(postMapper::postToDTO)
                .toList();
    }

    @Override
    @Transactional
    public PostResponseDTO getPost(Long postId) {
        PostEntity postEntity = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));

        List<PostCommentEntity> allComments = postCommentRepository.findAllByPostIdOrderByRootIdAscIdAsc(postId);

        List<PostCommentResponseDTO> commentDTOs = allComments.stream()
                .map(postCommentMapper::commentToDTO)
                .toList();

        return postMapper.postToDTO(postEntity, commentDTOs);
    }

    @Override
    @Transactional
    public PostResponseDTO updatePost(Long postId, PostUpdateRequestDTO postUpdateRequestDTO) {
        PostEntity postEntity = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));
        postEntity.update(
                postUpdateRequestDTO.title(),
                postUpdateRequestDTO.contents()
        );
        return postMapper.postToDTO(postEntity);
    }

    @Override
    public void deletePost(Long postId) {
        postCommentRepository.deleteAllByPostId(postId);
        postRepository.deleteById(postId);
    }

}
