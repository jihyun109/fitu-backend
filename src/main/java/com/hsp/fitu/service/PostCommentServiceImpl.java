package com.hsp.fitu.service;

import com.hsp.fitu.dto.PostCommentCreateRequestDTO;
import com.hsp.fitu.dto.PostCommentResponseDTO;
import com.hsp.fitu.dto.PostCommentUpdateRequestDTO;
import com.hsp.fitu.entity.PostCommentsEntity;
import com.hsp.fitu.entity.UserEntity;
import com.hsp.fitu.entity.enums.Role;
import com.hsp.fitu.mapper.PostCommentMapper;
import com.hsp.fitu.repository.PostCommentRepository;
import com.hsp.fitu.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
public class PostCommentServiceImpl implements PostCommentService{
    private final PostCommentRepository postCommentRepository;
    private final PostCommentMapper postCommentMapper;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public PostCommentResponseDTO createComment(Long postId, PostCommentCreateRequestDTO req, Long writerId) {
        PostCommentsEntity saved;

        if (req.rootId() == null) {
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
                    boolean canView = !c.isSecret() ||
                            Objects.equals(getUserNameById(currentUserId), c.writerName()) ||
                            Objects.equals(currentUserId, postWriterId) ||
                            isAdmin(currentUserId) ||
                            isParentWriter(c, currentUserId);

                    PostCommentResponseDTO dto = new PostCommentResponseDTO(
                            c.id(),
                            c.writerName(),
                            c.writerProfileImgUrl(),
                            c.rootId(),
                            canView ? c.contents() : null,
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
    private boolean isAdmin(Long userId) {
        return userRepository.findById(userId)
                .map(u -> u.getRole().equals(Role.ADMIN))
                .orElse(false);
    }
    private String getUserNameById(Long userId) {
        return userRepository.findById(userId)
                .map(UserEntity::getName)
                .orElse("Unknown");
    }

    private boolean isParentWriter(PostCommentResponseDTO dto, Long currentUserId) {
        if (Objects.equals(dto.id(), dto.rootId())) {
            return false; // 루트 댓글이면 부모 아님
        }
        PostCommentsEntity rootComment = postCommentRepository.findById(dto.rootId())
                .orElse(null);
        return rootComment != null && Objects.equals(rootComment.getWriterId(), currentUserId);
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
