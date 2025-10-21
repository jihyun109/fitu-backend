package com.hsp.fitu.service;

import com.hsp.fitu.dto.UserFriendCodeResponseDto;
import com.hsp.fitu.dto.UserInfoRequestDTO;
import com.hsp.fitu.dto.UserProfileImageResponseDto;

public interface UserService {
    void saveInfo(Long userId, UserInfoRequestDTO userInfoRequestDTO);

    UserProfileImageResponseDto findUserProfileImageAndVisibility(Long userId);

    UserFriendCodeResponseDto getFriendCode(Long userId);

    void deactivateUser(Long userId);
}
