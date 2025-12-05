package com.hsp.fitu.service;

import com.hsp.fitu.dto.AdminWorkoutVerifyDetailResponseDTO;
import com.hsp.fitu.dto.AdminWorkoutVerifyResponseDTO;
import com.hsp.fitu.dto.PageResponseDTO;

public interface AdminWorkoutVerifyService {

    PageResponseDTO<AdminWorkoutVerifyResponseDTO> getPendingVerify(int page, int size);

    AdminWorkoutVerifyDetailResponseDTO getVerifyDetail(Long id);

    void acceptedVerify(Long id, int weight);

    void deleteVerifyId(long verifyId);

}
