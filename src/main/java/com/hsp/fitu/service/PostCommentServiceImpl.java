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

    @Override
    @Transactional
    public PostCommentResponseDTO createComment(PostCommentCreateRequestDTO req, Long writerId) {
        PostEntity post = postRepository.findById(req.getPostId())
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));

        if (req.getTargetCommentId() == null) {
            // 최상위: 먼저 저장하여 id 발급
            PostCommentEntity root = postCommentRepository.save(
                    PostCommentEntity.builder()
                            .postId(post.getId())
                            .writerId(writerId)
                            .contents(req.getContents())
                            .build()
            );
            root.setRootId(root.getId());
            PostCommentEntity saved = postCommentRepository.save(root);
            return PostCommentMapper.commentToDTO(saved);
        } else {
            // 답글: 대상의 루트 구해 동일 스레드로 묶기
            PostCommentEntity target = postCommentRepository.findById(req.getTargetCommentId())
                    .orElseThrow(() -> new IllegalArgumentException("대상 댓글을 찾을 수 없습니다."));

            if (!target.getPostId().equals(req.getPostId())) {
                throw new IllegalArgumentException("다른 게시글에 답글을 달 수 없습니다.");
            }

            Long threadRootId = target.getRootId() != null ? target.getRootId() : target.getId();

            PostCommentEntity saved = postCommentRepository.save(
                    PostCommentEntity.builder()
                            .postId(post.getId())
                            .writerId(writerId)
                            .contents(req.getContents())
                            .rootId(threadRootId)
                            .build()
            );
            return PostCommentMapper.commentToDTO(saved);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<PostCommentResponseDTO> getCommentsByPost(Long postId) {
        return postCommentRepository.findByPostIdOrderByRootIdAscCreatedAtAsc(postId)
                .stream().map(PostCommentMapper::commentToDTO).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<PostCommentResponseDTO> getThreadByRoot(Long rootId) {
        return postCommentRepository.findByRootIdOrderByCreatedAtAsc(rootId)
                .stream().map(PostCommentMapper::commentToDTO).toList();
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
