package com.hsp.fitu.controller;

import com.hsp.fitu.dto.PostCreateRequestDTO;
import com.hsp.fitu.dto.PostResponseDTO;
import com.hsp.fitu.dto.PostUpdateRequestDTO;
import com.hsp.fitu.jwt.CustomUserDetails;
import com.hsp.fitu.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/post")
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
    public ResponseEntity<List<PostResponseDTO>> getAllPosts() {
        return ResponseEntity.ok(postService.getAllPosts());
    }

    @GetMapping("/{postId}")
    public ResponseEntity<PostResponseDTO> getPost(@PathVariable Long postId) {
        return ResponseEntity.ok(postService.getPost(postId));
    }


    @PatchMapping("/{postId}")
    public ResponseEntity<PostResponseDTO> updatePost(@PathVariable Long postId,
                                                      @RequestBody PostUpdateRequestDTO postUpdateRequestDTO) {
        return ResponseEntity.ok(postService.updatePost(postId, postUpdateRequestDTO));
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<Void> deletePost(@PathVariable Long postId) {
        postService.deletePost(postId);
        return ResponseEntity.noContent().build();
    }
}
