package com.hsp.fitu.controller;

import com.hsp.fitu.dto.admin.AdminReportResponseDTO;
import com.hsp.fitu.dto.SliceResponseDTO;
import com.hsp.fitu.service.AdminReportService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v2/admin")
public class AdminPostReportController {
    private final AdminReportService adminReportService;

    @Operation(summary = "요청문의/게시글 신고 by 조민기")
    @GetMapping("/reports")
    public ResponseEntity<SliceResponseDTO<AdminReportResponseDTO>> getReportPosts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size
    ) {

        SliceResponseDTO<AdminReportResponseDTO> reportedPosts =
                adminReportService.getReportedPosts(page, size);

        return ResponseEntity.ok(reportedPosts);
    }
}
