package com.hsp.fitu.service;

import com.hsp.fitu.dto.PostCommentCreateRequestDTO;
import com.hsp.fitu.dto.PostCommentResponseDTO;
import com.hsp.fitu.dto.PostCommentUpdateRequestDTO;
import com.hsp.fitu.entity.PostCommentsEntity;
import com.hsp.fitu.mapper.PostCommentMapper;
import com.hsp.fitu.repository.PostCommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PostCommentServiceImpl implements PostCommentService{
    private final PostCommentRepository postCommentRepository;
    private final PostCommentMapper postCommentMapper;

    @Override
    @Transactional
    public PostCommentResponseDTO createComment(Long postId, PostCommentCreateRequestDTO req, Long writerId) {

        if (req.rootId() == null) {
            PostCommentsEntity root = postCommentRepository.save(
                    PostCommentsEntity.builder()
                            .postId(postId)
                            .writerId(writerId)
                            .contents(req.contents())
                            .rootId(-1L)
                            .build()
            );

            root.setRootId(root.getId());
            PostCommentsEntity saved = postCommentRepository.save(root);
            return postCommentMapper.commentToDTO(saved);

        } else {
            Long threadRootId = req.rootId();

            PostCommentsEntity saved = postCommentRepository.save(
                    PostCommentsEntity.builder()
                            .postId(postId)
                            .writerId(writerId)
                            .contents(req.contents())
                            .rootId(threadRootId)
                            .build()
            );
            return postCommentMapper.commentToDTO(saved);
        }
    }

    @Override
    @Transactional
    public PostCommentResponseDTO updateComment(Long postId, Long commentId, PostCommentUpdateRequestDTO commentUpdateRequestDTO) {
        PostCommentsEntity postCommentsEntity = postCommentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("댓글을 찾을 수 없습니다."));
        postCommentsEntity.update(commentUpdateRequestDTO.contents());
        return postCommentMapper.commentToDTO(postCommentsEntity);
    }

    @Override
    @Transactional
    public void deleteComment(Long postId, Long commentId) {
        if(!postCommentRepository.existsById(commentId)) {
            throw new IllegalArgumentException("댓글을 찾을 수 없습니다.");
        }
        postCommentRepository.deleteById(commentId);
    }
}
