package com.hsp.fitu.service;

import com.hsp.fitu.dto.AdminReportResponseDTO;
import com.hsp.fitu.dto.SliceResponseDTO;
import com.hsp.fitu.repository.AdminPostReportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AdminReportServiceImpl implements AdminReportService{
    private final AdminPostReportRepository adminPostReportRepository;

    @Override
    @Transactional(readOnly = true)
    public SliceResponseDTO<AdminReportResponseDTO> getReportedPosts(int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"));//recordedAt
        Page<AdminReportResponseDTO> result = adminPostReportRepository.findReportedPosts(pageRequest);

        return new SliceResponseDTO<> (
                result.getContent(),
                result.hasNext()
        );
    }
}
