package com.hsp.fitu.controller;

import com.hsp.fitu.dto.PostCreateRequestDTO;
import com.hsp.fitu.dto.PostResponseDTO;
import com.hsp.fitu.dto.PostUpdateRequestDTO;
import com.hsp.fitu.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/post")
public class PostController {
    private final PostService postService;

    @GetMapping
    public List<PostResponseDTO> getAllPosts() {
        return postService.getAllPosts();
    }

    @GetMapping("/{id}")
    public ResponseEntity<PostResponseDTO> getPostById(@PathVariable Long id) {
        return postService.getPostById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public PostResponseDTO createPost(@RequestBody PostCreateRequestDTO postCreateRequestDTO) {
        return postService.createPost(postCreateRequestDTO);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PostResponseDTO> updatePost(@PathVariable Long id, @RequestBody PostUpdateRequestDTO postUpdateRequestDTO) {
        PostResponseDTO result = postService.updatePost(id, postUpdateRequestDTO);
        if(result != null) {
            return ResponseEntity.ok(result);
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePost(@PathVariable Long id) {
        postService.deletePost(id);
        return ResponseEntity.ok().build();
    }
}
