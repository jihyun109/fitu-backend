package com.hsp.fitu.service;

import com.hsp.fitu.dto.AdminReportResponseDTO;

import java.util.List;

public interface AdminReportService {
    List<AdminReportResponseDTO> getReportedPosts();
    void deletePost(long postId);
}
