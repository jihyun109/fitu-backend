package com.hsp.fitu.service;

import com.hsp.fitu.dto.admin.AdminUserReportResponseDTO;
import com.hsp.fitu.dto.SliceResponseDTO;
import com.hsp.fitu.repository.AdminUserReportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminUserReportServiceImpl implements AdminUserReportService{
    private final AdminUserReportRepository adminUserReportRepository;

    @Override
    public SliceResponseDTO<AdminUserReportResponseDTO> getReportedUser(int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "recordedAt"));
        Page<AdminUserReportResponseDTO> result = adminUserReportRepository.findReportedUser(pageRequest);

        return new SliceResponseDTO<> (
                result.getContent(),
                result.hasNext()
        );
    }
}