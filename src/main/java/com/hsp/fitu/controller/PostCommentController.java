package com.hsp.fitu.controller;

import com.hsp.fitu.dto.PostCommentCreateRequestDTO;
import com.hsp.fitu.dto.PostCommentResponseDTO;
import com.hsp.fitu.dto.PostCommentUpdateRequestDTO;
import com.hsp.fitu.service.PostCommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/comments")
public class PostCommentController {
    private final PostCommentService postCommentService;

    @PostMapping
    public ResponseEntity<PostCommentResponseDTO> createComment(@RequestBody PostCommentCreateRequestDTO commentCreateRequestDTO) {
        return ResponseEntity.ok(postCommentService.createComment(commentCreateRequestDTO));
    }

    @GetMapping("/{id}")
    public ResponseEntity<List<PostCommentResponseDTO>> getCommentsByPost(@PathVariable Long postId) {
        return ResponseEntity.ok(postCommentService.getCommentsByPost(postId));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<PostCommentResponseDTO> updateComment(
            @PathVariable Long id,
            @RequestBody PostCommentUpdateRequestDTO commentUpdateRequestDTO) {
        return ResponseEntity.ok(postCommentService.updateComment(id, commentUpdateRequestDTO));
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteComment(@PathVariable Long id) {
        postCommentService.deleteComment(id);
        return ResponseEntity.noContent().build();
    }
}
