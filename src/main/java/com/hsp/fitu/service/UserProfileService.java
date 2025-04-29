package com.hsp.fitu.service;

import com.hsp.fitu.dto.UserProfileRequestDTO;

public interface UserProfileService {
    void inputProfileOnce(long userId, UserProfileRequestDTO dto);
}