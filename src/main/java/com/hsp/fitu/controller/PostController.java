package com.hsp.fitu.controller;

import com.hsp.fitu.dto.*;
import com.hsp.fitu.entity.enums.PostCategory;
import com.hsp.fitu.jwt.CustomUserDetails;
import com.hsp.fitu.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v2/posts")
public class PostController {
    private final PostService postService;

    @PostMapping
    public ResponseEntity<PostResponseDTO> createPost(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody PostCreateRequestDTO requestDTO) {

        Long writerId = userDetails.getId();
        Long universityId = userDetails.getUniversityId();

        PostResponseDTO postResponseDTO = postService.createPost(writerId, universityId, requestDTO);
        return ResponseEntity.ok(postResponseDTO);
    }

    @GetMapping
    public ResponseEntity<PostSliceResponseDTO<PostListResponseDTO>> getAllPosts(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam PostCategory category,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        Long universityId = userDetails.getUniversityId();

        PostSliceResponseDTO<PostListResponseDTO> postResponseDTOs = postService.getAllPosts(category, universityId, page, size);
        return ResponseEntity.ok(postResponseDTOs);
    }

    @GetMapping("/{postId}")
    public ResponseEntity<PostResponseDTO> getPost(
            @PathVariable Long postId) {
        return ResponseEntity.ok(postService.getPost(postId));
    }

    @GetMapping("/search")
    public ResponseEntity<PostSliceResponseDTO<PostListResponseDTO>> searchPosts(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam PostCategory category,
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        Long universityId = userDetails.getUniversityId();

        PostSliceResponseDTO<PostListResponseDTO> postResponseDTOs = postService.searchPosts(universityId, category, keyword, page, size);

        return ResponseEntity.ok(postResponseDTOs);
    }

    @PatchMapping("/{postId}")
    public ResponseEntity<PostResponseDTO> updatePost(
            @PathVariable Long postId,
            @RequestBody PostUpdateRequestDTO postUpdateRequestDTO) {
        return ResponseEntity.ok(postService.updatePost(postId, postUpdateRequestDTO));
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<Void> deletePost(
            @PathVariable Long postId) {
        postService.deletePost(postId);
        return ResponseEntity.noContent().build();
    }
}