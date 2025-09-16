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

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/comment")
public class PostCommentController {
    private final PostCommentService postCommentService;

    @PostMapping
    public ResponseEntity<PostCommentResponseDTO> createComment(@RequestBody PostCommentCreateRequestDTO postCommentCreateRequestDTO,
                                                                @AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(postCommentService.createComment(postCommentCreateRequestDTO, userDetails.getId()));
    }

    @GetMapping
    public ResponseEntity<List<PostCommentResponseDTO>> getCommentsByPost(@RequestParam Long postId) {
        return ResponseEntity.ok(postCommentService.getCommentsByPost(postId));
    }

    @GetMapping("/threads/{rootId}")
    public ResponseEntity<List<PostCommentResponseDTO>> getThread(@PathVariable Long rootId) {
        return ResponseEntity.ok(postCommentService.getThreadByRoot(rootId));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<PostCommentResponseDTO> updateComment(
            @PathVariable Long id,
            @RequestBody PostCommentUpdateRequestDTO commentUpdateRequestDTO) {
        return ResponseEntity.ok(postCommentService.updateComment(id, commentUpdateRequestDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteComment(@PathVariable Long id) {
        postCommentService.deleteComment(id);
        return ResponseEntity.noContent().build();
    }
}
