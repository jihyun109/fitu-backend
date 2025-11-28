package com.hsp.fitu.service;

import com.hsp.fitu.dto.UserFriendCodeResponseDto;
import com.hsp.fitu.dto.UserInfoRequestDTO;
import com.hsp.fitu.dto.UserProfileImageResponseDto;
import com.hsp.fitu.dto.UserSaveInfoResponseDTO;

public interface UserService {
    UserSaveInfoResponseDTO saveInfo(Long userId, UserInfoRequestDTO userInfoRequestDTO, String authHeader);

    UserProfileImageResponseDto findUserProfileImageAndVisibility(Long userId);

    UserFriendCodeResponseDto getFriendCode(Long userId);

    void deactivateUser(Long userId);
}
