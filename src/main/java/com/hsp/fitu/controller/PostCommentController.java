package com.hsp.fitu.controller;

import com.hsp.fitu.dto.PostCommentCreateRequestDTO;
import com.hsp.fitu.dto.PostCommentResponseDTO;
import com.hsp.fitu.dto.PostCommentUpdateRequestDTO;
import com.hsp.fitu.jwt.CustomUserDetails;
import com.hsp.fitu.service.PostCommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v2/posts")
public class PostCommentController {
    private final PostCommentService postCommentService;

    @PostMapping("/{postId}/comments")
    public ResponseEntity<PostCommentResponseDTO> createComment(
            @PathVariable Long postId,
            @RequestBody PostCommentCreateRequestDTO postCommentCreateRequestDTO,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        Long writerId = userDetails.getId();
        return ResponseEntity.ok(postCommentService.createComment(postId, postCommentCreateRequestDTO, writerId));
    }

    @PatchMapping("/{postId}/comments/{commentId}")
    public ResponseEntity<PostCommentResponseDTO> updateComment(
            @PathVariable Long postId,
            @PathVariable Long commentId,
            @RequestBody PostCommentUpdateRequestDTO commentUpdateRequestDTO) {
        return ResponseEntity.ok(postCommentService.updateComment(postId, commentId, commentUpdateRequestDTO));
    }

    @DeleteMapping("/{postId}/comments/{commentId}")
    public ResponseEntity<Void> deleteComment(
            @PathVariable Long postId,
            @PathVariable Long commentId) {

        postCommentService.deleteComment(postId, commentId);
        return ResponseEntity.noContent().build();
    }
}
