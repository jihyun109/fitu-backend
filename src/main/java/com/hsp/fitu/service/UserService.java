package com.hsp.fitu.service;

import com.hsp.fitu.dto.UserInfoRequestDTO;
import com.hsp.fitu.dto.UserProfileImageResponseDto;

public interface UserService {
    void saveInfo(Long userId, UserInfoRequestDTO userInfoRequestDTO);

    UserProfileImageResponseDto findUserProfileImageAndVisibility(Long userId);

    String getFriendCode(Long userId);
}
