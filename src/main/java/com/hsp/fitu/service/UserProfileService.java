package com.hsp.fitu.service;

import com.hsp.fitu.dto.AdditionalInfoResponseDTO;
import com.hsp.fitu.dto.UserProfileRequestDTO;

public interface UserProfileService {
    AdditionalInfoResponseDTO inputProfileOnce(long userId, UserProfileRequestDTO dto);
}