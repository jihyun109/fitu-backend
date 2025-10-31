package com.hsp.fitu.service;

import com.hsp.fitu.dto.AdminReportResponseDTO;
import com.hsp.fitu.entity.ReportsEntity;
import com.hsp.fitu.entity.enums.TargetType;
import com.hsp.fitu.repository.AdminPostReportRepository;
import com.hsp.fitu.repository.PostCommentRepository;
import com.hsp.fitu.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminReportServiceImpl implements AdminReportService{
    private final AdminPostReportRepository adminPostReportRepository;
    private final PostRepository postRepository;
    private final PostCommentRepository postCommentRepository;

    @Override
    @Transactional(readOnly = true)
    public List<AdminReportResponseDTO> getReportedPosts() {
        List<ReportsEntity> reports = adminPostReportRepository.findByTargetType(TargetType.POST);

        return reports.stream()
                .map(r -> new AdminReportResponseDTO(

                        r.getReporterId(),
                        r.getReporterAt(),
                        r.getUniversityName(),
                        r.getPostTitle()
                ))
                .toList();
    }

    @Override
    @Transactional
    public void deletePost(long postId) {
        postCommentRepository.deleteByPostId(postId);
        postRepository.deleteById(postId);
    }
}
