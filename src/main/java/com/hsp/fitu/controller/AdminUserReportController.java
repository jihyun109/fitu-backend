package com.hsp.fitu.controller;

import com.hsp.fitu.dto.AdminUserReportResponseDTO;
import com.hsp.fitu.dto.SliceResponseDTO;
import com.hsp.fitu.service.AdminUserReportService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v2/admin")
public class AdminUserReportController {
    private final AdminUserReportService adminUserReportService;

    @Operation(summary = "요청 문의/회원신고 by 조민기")
    @GetMapping("/reportUser")
    public ResponseEntity<SliceResponseDTO<AdminUserReportResponseDTO>> getReportUser(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size
    ) {
        SliceResponseDTO<AdminUserReportResponseDTO> reportedPosts =
                adminUserReportService.getReportedUser(page, size);

        return ResponseEntity.ok(reportedPosts);
    }
}