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

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PostCommentServiceImpl implements PostCommentService{
    private final PostCommentRepository postCommentRepository;
    private final PostRepository postRepository;
    private final PostCommentMapper postCommentMapper;

    @Override
    @Transactional
    public PostCommentResponseDTO createComment(PostCommentCreateRequestDTO req, Long writerId) {
        PostEntity postEntity = postRepository.findById(req.postId())
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));

        if (req.rootId() == null) {

            PostCommentEntity root = postCommentRepository.save(
                    PostCommentEntity.builder()
                            .postId(postEntity.getId())
                            .writerId(writerId)
                            .contents(req.contents())
                            .rootId(-1L)
                            .build()
            );

            root.setRootId(root.getId());
            PostCommentEntity saved = postCommentRepository.save(root);

            return postCommentMapper.commentToDTO(saved);

        } else {

            PostCommentEntity rootComment = postCommentRepository.findById(req.rootId())
                    .orElseThrow(() -> new IllegalArgumentException("대상이 되는 루트 댓글을 찾을 수 없습니다."));

            if (!rootComment.getPostId().equals(req.postId())) {
                throw new IllegalArgumentException("다른 게시글의 댓글에 답글을 달 수 없습니다.");
            }

            // req.rootId()를 그대로 사용
            Long threadRootId = req.rootId();

            PostCommentEntity saved = postCommentRepository.save(
                    PostCommentEntity.builder()
                            .postId(postEntity.getId())
                            .writerId(writerId)
                            .contents(req.contents())
                            .rootId(threadRootId)
                            .build()
            );
            return postCommentMapper.commentToDTO(saved);
        }
    }

    @Override
    public PostCommentResponseDTO updateComment(Long id, PostCommentUpdateRequestDTO commentUpdateRequestDTO) {
        PostCommentEntity postCommentEntity = postCommentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("댓글을 찾을 수 없습니다."));
        postCommentEntity.update(commentUpdateRequestDTO.contents());
        return postCommentMapper.commentToDTO(postCommentEntity);
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
