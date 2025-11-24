package com.hsp.fitu.service;

import com.hsp.fitu.dto.admin.AdminWorkoutVerifyDetailResponseDTO;
import com.hsp.fitu.dto.admin.AdminWorkoutVerifyResponseDTO;
import com.hsp.fitu.dto.admin.PageResponseDTO;
import com.hsp.fitu.entity.MediaFilesEntity;
import com.hsp.fitu.entity.WorkoutVerificationEntity;
import com.hsp.fitu.entity.enums.WorkoutVerificationRequestStatus;
import com.hsp.fitu.repository.AdminWorkoutVerifyRepository;
import com.hsp.fitu.repository.MediaFilesRepository;
import com.hsp.fitu.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminWorkoutVerifyServiceImpl implements AdminWorkoutVerifyService{
    private final AdminWorkoutVerifyRepository adminWorkoutVerifyRepository;
    private final UserRepository userRepository;
    private final MediaFilesRepository mediaFilesRepository;

    @Override
    @Transactional(readOnly = true)
    public PageResponseDTO<AdminWorkoutVerifyResponseDTO> getPendingVerify(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("requestDate").descending());

        Page<WorkoutVerificationEntity> result =
                adminWorkoutVerifyRepository.findAllByStatus(WorkoutVerificationRequestStatus.PENDING, pageable);

        List<AdminWorkoutVerifyResponseDTO> contents = result.getContent().stream()
                .map(e -> new AdminWorkoutVerifyResponseDTO(
                        e.getId(),
                        userRepository.findNameById(e.getUserId()),
                        e.getRequestDate(),
                        e.getWorkoutType().name())).toList();

        return new PageResponseDTO<>(
                contents,
                result.getNumber(),
                result.getSize(),
                result.getTotalElements(),
                result.getTotalPages(),
                result.isLast()
        );
    }

    @Override
    @Transactional(readOnly = true)
    public AdminWorkoutVerifyDetailResponseDTO getVerifyDetail(Long id) {
        WorkoutVerificationEntity workoutVerificationEntity = adminWorkoutVerifyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Verification not found"));

        String name = userRepository.findNameById(workoutVerificationEntity.getUserId());

        MediaFilesEntity mediaFiles = mediaFilesRepository.findById(workoutVerificationEntity.getVideoId())
                .orElseThrow(() -> new RuntimeException("Video not found"));

        return new AdminWorkoutVerifyDetailResponseDTO(
                workoutVerificationEntity.getId(),
                name,
                workoutVerificationEntity.getRequestDate(),
                workoutVerificationEntity.getWorkoutType().name(),
                mediaFiles.getUrl()
        );
    }

    @Override
    @Transactional(readOnly = true)
    public void acceptedVerify(Long id, int weight) {
        WorkoutVerificationEntity entity = adminWorkoutVerifyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Verification not found"));

        entity.accepted(weight);
    }

    @Override
    @Transactional
    public void deleteVerifyId(long verifyId) {
        adminWorkoutVerifyRepository.deleteById(verifyId);
    }
}
