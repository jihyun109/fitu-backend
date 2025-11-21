package com.hsp.fitu.service;

import com.hsp.fitu.entity.WorkoutVerificationEntity;

import java.util.List;

public interface AdminWorkoutVerifyService {

    List<WorkoutVerificationEntity> getPendingVerify();

    WorkoutVerificationEntity getVerifyDetail(Long id);

    void approveVerify(Long id, int weight);

    void deleteVerify(long id);

}
