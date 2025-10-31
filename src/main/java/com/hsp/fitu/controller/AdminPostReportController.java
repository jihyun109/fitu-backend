package com.hsp.fitu.controller;

import com.hsp.fitu.dto.AdminReportResponseDTO;
import com.hsp.fitu.service.AdminReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v2/admin/reports")
public class AdminPostReportController {
    private final AdminReportService adminReportService;
    @GetMapping
    public ResponseEntity<List<AdminReportResponseDTO>> getReportPosts() {
        return ResponseEntity.ok(adminReportService.getReportedPosts());
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<Void> deleteReportedPost(
            @PathVariable long postId) {
        adminReportService.deletePost(postId);
        return ResponseEntity.noContent().build();
    }
}
