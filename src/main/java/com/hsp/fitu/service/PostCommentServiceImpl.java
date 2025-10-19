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

import java.util.List;
import java.util.Objects;

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
                            .build()
            );
        }
        return postCommentMapper.commentToDTO(saved);
    }

    @Override
    @Transactional
    public List<PostCommentResponseDTO> getComments(Long postId, Long currentUserId, Long postWriterId) {
        List<PostCommentResponseDTO> rawComments = postCommentRepository.findCommentsByPostId(postId);

        return rawComments.stream()
                .filter(c -> {
                    if (!c.isSecret()) return true; //공개 댓글이라면 항상 통과

                    return Objects.equals(getUserNameById(currentUserId),c.writerName()) ||
                           Objects.equals(currentUserId, postWriterId) ||
                           isAdmin(currentUserId);
                })
                .map(c -> new PostCommentResponseDTO(
                        c.id(),
                        c.writerName(),
                        c.writerProfileImgUrl(),
                        c.rootId(),
                        c.contents(),
                        c.createdAt(),
                        Objects.equals(c.writerName(), getUserNameById(postWriterId)),
                        c.isSecret()
                ))
                .toList();

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
