package com.hsp.fitu.controller;

import com.hsp.fitu.dto.ReportRequestDTO;
import com.hsp.fitu.dto.ReportResponseDTO;
import com.hsp.fitu.jwt.CustomUserDetails;
import com.hsp.fitu.service.ReportService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v2/reports")
public class ReportController {
    private final ReportService reportService;

    @PostMapping
    @Operation(summary = "신고하기 by 조민기")
    public ReportResponseDTO report(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody ReportRequestDTO reportRequestDTO) {

        Long reporterId = userDetails.getId();

        return reportService.report(reporterId, reportRequestDTO);
    }
}
