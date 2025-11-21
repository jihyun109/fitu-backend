package com.hsp.fitu.service;

import com.hsp.fitu.dto.admin.AdminPostManagementRequestDTO;
import com.hsp.fitu.dto.admin.AdminPostManagementResponseDTO;
import com.hsp.fitu.dto.PostSliceResponseDTO;
import com.hsp.fitu.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AdminPostManagementServiceImpl implements AdminPostManagementService{
    private final PostRepository postRepository;

    @Override
    @Transactional(readOnly = true)
    public PostSliceResponseDTO<AdminPostManagementResponseDTO> getPostsByUniversity(AdminPostManagementRequestDTO requestDTO, int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"));

        Page<AdminPostManagementResponseDTO> resultPage =
                postRepository.findPostsByUniversityName(requestDTO.universityName(), pageRequest);

        return new PostSliceResponseDTO<>(
                requestDTO.universityName(),
                resultPage.getContent(),
                resultPage.hasNext()
        );
    }
}