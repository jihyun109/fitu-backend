package com.hsp.fitu.service;

import com.hsp.fitu.entity.WorkoutVerificationEntity;
import com.hsp.fitu.entity.enums.WorkoutVerificationRequestStatus;
import com.hsp.fitu.repository.AdminWorkoutVerifyRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminWorkoutVerifyServiceImpl implements AdminWorkoutVerifyService{
    private final AdminWorkoutVerifyRepository adminWorkoutVerifyRepository;

    @Override
    @Transactional(readOnly = true)
    public List<WorkoutVerificationEntity> getPendingVerify() {
        return adminWorkoutVerifyRepository.findAllByStatus(WorkoutVerificationRequestStatus.PENDING);
    }

    @Override
    @Transactional(readOnly = true)
    public WorkoutVerificationEntity getVerifyDetail(Long id) {
        return adminWorkoutVerifyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Verification not found"));
    }

    @Override
    @Transactional(readOnly = true)
    public void approveVerify(Long id, int weight) {
        WorkoutVerificationEntity entity = adminWorkoutVerifyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Verification not found"));

    }

    @Override
    public void deleteVerify(long id) {
        Workout
    }
}
public void deletePost(long postId) {
    postCommentRepository.deleteByPostId(postId);
    postRepository.deleteById(postId);
}