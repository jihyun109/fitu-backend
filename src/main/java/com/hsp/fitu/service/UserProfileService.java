package com.hsp.fitu.service;

import com.hsp.fitu.dto.UserProfileRequestDTO;
import com.hsp.fitu.dto.UserProfileResponseDTO;
import com.hsp.fitu.entity.UserEntity;

public interface UserProfileService {
    UserProfileResponseDTO inputProfileOnce(UserProfileRequestDTO dto);
    UserEntity getUserById(Long userId);
}
