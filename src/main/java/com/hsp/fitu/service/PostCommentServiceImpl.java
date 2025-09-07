package com.hsp.fitu.service;

import com.hsp.fitu.dto.PostCommentCreateRequestDTO;
import com.hsp.fitu.dto.PostCommentResponseDTO;
import com.hsp.fitu.dto.PostCommentUpdateRequestDTO;
import com.hsp.fitu.entity.PostCommentEntity;
import com.hsp.fitu.entity.PostEntity;
import com.hsp.fitu.mapper.PostCommentMapper;
import com.hsp.fitu.repository.PostCommentRepository;
import com.hsp.fitu.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PostCommentServiceImpl implements PostCommentService{
    private final PostCommentRepository postCommentRepository;
    private final PostRepository postRepository;

    @Override
    @Transactional
    public PostCommentResponseDTO createComment(PostCommentCreateRequestDTO commentCreateRequestDTO) {
        PostEntity postEntity = postRepository.findById(commentCreateRequestDTO.getPostId())
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));


        PostCommentEntity postCommentEntity = PostCommentEntity.builder()
                .writerId(commentCreateRequestDTO.getWriterId())
                .postId(postEntity.getId())
                .contents(commentCreateRequestDTO.getContents())
                .build();

        PostCommentEntity saved = postCommentRepository.save(postCommentEntity);
        return PostCommentMapper.commentToDTO(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PostCommentResponseDTO> getCommentsByPost(Long postId) {
        return postCommentRepository.findByPostIdAndParentIsNull(postId)
                .stream()
                .map(PostCommentMapper::commentToDTO)
                .toList();
    }

    @Override
    public PostCommentResponseDTO updateComment(Long id, PostCommentUpdateRequestDTO commentUpdateRequestDTO) {
        PostCommentEntity postCommentEntity = postCommentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("댓글을 찾을 수 없습니다."));
        postCommentEntity.update(commentUpdateRequestDTO.getContents());
        return PostCommentMapper.commentToDTO(postCommentEntity);
    }

    @Override
    @Transactional
    public void deleteComment(Long id) {
        if(!postCommentRepository.existsById(id)) {
            throw new IllegalArgumentException("댓글을 찾을 수 없습니다.");
        }
        postCommentRepository.deleteById(id);
    }
}
