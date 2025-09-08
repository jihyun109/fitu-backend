package com.hsp.fitu.controller;

import com.hsp.fitu.dto.ReportRequestDTO;
import com.hsp.fitu.dto.ReportResponseDTO;
import com.hsp.fitu.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/reports")
public class ReportController {
    private final ReportService reportService;

    @PostMapping
    public ReportResponseDTO report(@RequestBody ReportRequestDTO reportRequestDTO) {
        return reportService.report(reportRequestDTO);
    }
}
