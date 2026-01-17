package com.hsp.fitu.service;

import com.hsp.fitu.dto.PostCommentCreateRequestDTO;
import com.hsp.fitu.dto.PostCommentResponseDTO;
import com.hsp.fitu.entity.PostCommentsEntity;
import com.hsp.fitu.entity.UserEntity;
import com.hsp.fitu.error.BusinessException;
import com.hsp.fitu.error.ErrorCode;
import com.hsp.fitu.repository.PostCommentRepository;
import com.hsp.fitu.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
public class PostCommentServiceImpl implements PostCommentService {
    private final PostCommentRepository postCommentRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public PostCommentResponseDTO createComment(Long postId, PostCommentCreateRequestDTO req, Long writerId) {
        PostCommentsEntity saved;

        if (req.rootId() == 0) {
            PostCommentsEntity root = postCommentRepository.save(
                    PostCommentsEntity.builder()
                            .postId(postId)
                            .writerId(writerId)
                            .contents(req.contents())
                            .rootId(-1L)
                            .isSecret(req.isSecret())
                            .build()
            );

            root.setRootId(root.getId());
            saved = postCommentRepository.save(root);

        } else {
            saved = postCommentRepository.save(
                    PostCommentsEntity.builder()
                            .postId(postId)
                            .writerId(writerId)
                            .contents(req.contents())
                            .rootId(req.rootId())
                            .isSecret(req.isSecret())
                            .build()
            );
        }
        return postCommentRepository.findCommentDTOById(saved.getId());
    }

    @Override
    @Transactional
    public List<PostCommentResponseDTO> getComments(Long postId, Long currentUserId, Long postWriterId) {
        List<PostCommentResponseDTO> rawComments = postCommentRepository.findCommentsByPostId(postId);

        Map<Long, PostCommentResponseDTO> commentMap = new HashMap<>();

        List<PostCommentResponseDTO> processed = rawComments.stream()
                .map(c -> {
                    PostCommentResponseDTO dto = new PostCommentResponseDTO(
                            c.id(),
                            currentUserId,
                            c.writerId(),
                            c.writerName(),
                            c.writerProfileImgUrl(),
                            c.rootId(),
                            c.contents(),
                            c.createdAt(),
                            Objects.equals(c.writerName(), getUserNameById(postWriterId)),
                            c.isSecret(),
                            new ArrayList<>()
                    );
                    commentMap.put(dto.id(), dto);
                    return dto;
                }).toList();

        List<PostCommentResponseDTO> rootComments = new ArrayList<>();

        for (PostCommentResponseDTO dto : processed) {
            if (Objects.equals(dto.id(), dto.rootId())) {
                rootComments.add(dto);
            } else {
                PostCommentResponseDTO parent = commentMap.get(dto.rootId());
                if (parent != null && parent.replies() != null) {
                    parent.replies().add(dto);
                }
            }
        }

        return rootComments;
    }

    private String getUserNameById(Long userId) {
        return userRepository.findById(userId)
                .map(UserEntity::getName)
                .orElse("Unknown");
    }

    @Override
    @Transactional
    public void deleteComment(Long postId, Long commentId, Long writerId) {

        PostCommentsEntity postComments = postCommentRepository.findById(commentId)
                .orElseThrow(() -> new BusinessException(ErrorCode.COMMENT_NOT_FOUND));

        if (postComments.getWriterId() != writerId) {
            throw new BusinessException(ErrorCode.INVALID_COMMENT_AUTHOR);
        }

        postCommentRepository.deleteById(commentId);
    }
}
