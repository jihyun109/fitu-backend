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
@RequestMapping("/api/v1/comment")
public class PostCommentController {
    private final PostCommentService postCommentService;

    @PostMapping
    public ResponseEntity<PostCommentResponseDTO> createComment(@RequestBody PostCommentCreateRequestDTO postCommentCreateRequestDTO,
                                                                @AuthenticationPrincipal CustomUserDetails userDetails) {
        Long writerId = userDetails.getId();
        return ResponseEntity.ok(postCommentService.createComment(postCommentCreateRequestDTO, writerId));
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
