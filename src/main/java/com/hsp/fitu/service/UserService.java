package com.hsp.fitu.service;

import com.hsp.fitu.dto.UserInfoRequestDTO;

public interface UserService {
    void saveInfo(Long userId, UserInfoRequestDTO userInfoRequestDTO);
}
