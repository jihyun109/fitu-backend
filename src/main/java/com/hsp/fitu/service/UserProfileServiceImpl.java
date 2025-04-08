package com.hsp.fitu.service;

import com.hsp.fitu.dto.UserProfileRequestDTO;
import com.hsp.fitu.dto.UserProfileResponseDTO;
import com.hsp.fitu.entity.UserEntity;
import com.hsp.fitu.repository.UserProfileRepository;
import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional;

@Service
public class UserProfileServiceImpl implements UserProfileService {

    private final UserProfileRepository userProfileRepository;

    public UserProfileServiceImpl(UserProfileRepository userProfileRepository) {
        this.userProfileRepository = userProfileRepository;
    }

    @Override
    @Transactional
    public UserProfileResponseDTO inputProfileOnce(UserProfileRequestDTO dto) {
        UserEntity user = userProfileRepository.findById(dto.getUserId())
                .orElseThrow(() -> new RuntimeException("해당 사용자를 찾을 수 없습니다."));

        user.updateProfile(dto.getHeight(), dto.getWeight(), dto.getGender());

        return new UserProfileResponseDTO(user.getHeight(), user.getWeight(), user.getGender());
    }

    @Override
    public UserEntity getUserById(Long userId) {
        return userProfileRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("해당 사용자를 찾을 수 없습니다."));
    }
}
