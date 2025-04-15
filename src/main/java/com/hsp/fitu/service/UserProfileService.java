package com.hsp.fitu.service;

import com.hsp.fitu.dto.UserProfileRequestDTO;
import com.hsp.fitu.dto.UserProfileResponseDTO;
import com.hsp.fitu.dto.UserStatusDTO;

public interface UserProfileService {
    UserProfileResponseDTO inputProfileOnce(UserProfileRequestDTO dto);
    UserStatusDTO checkUserStatus(String kakaoEmail);
}
