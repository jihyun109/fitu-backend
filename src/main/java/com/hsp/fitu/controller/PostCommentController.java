package com.hsp.fitu.controller;

import com.hsp.fitu.dto.PostCommentCreateRequestDTO;
import com.hsp.fitu.dto.PostCommentResponseDTO;
import com.hsp.fitu.jwt.CustomUserDetails;
import com.hsp.fitu.service.PostCommentService;
import io.swagger.v3.oas.annotations.Operation;
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
    @Operation(summary = "댓글 작성 by 조민기")
    public ResponseEntity<PostCommentResponseDTO> createComment(
            @PathVariable Long postId,
            @RequestBody PostCommentCreateRequestDTO postCommentCreateRequestDTO,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        Long writerId = userDetails.getId();
        return ResponseEntity.ok(postCommentService.createComment(postId, postCommentCreateRequestDTO, writerId));
    }

    @DeleteMapping("/{postId}/comments/{commentId}")
    @Operation(summary = "댓글 삭제 by 조민기")
    public ResponseEntity<Void> deleteComment(
            @PathVariable Long postId,
            @PathVariable Long commentId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        Long writerId = userDetails.getId();

        postCommentService.deleteComment(postId, commentId, writerId);
        return ResponseEntity.noContent().build();
    }
}
