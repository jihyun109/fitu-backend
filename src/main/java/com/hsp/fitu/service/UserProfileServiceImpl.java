package com.hsp.fitu.service;

import com.hsp.fitu.dto.UserProfileRequestDTO;
import com.hsp.fitu.dto.UserProfileResponseDTO;
import com.hsp.fitu.dto.UserStatusDTO;
import com.hsp.fitu.entity.UserEntity;
import com.hsp.fitu.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserProfileServiceImpl implements UserProfileService {

    private final UserRepository userRepository;

    // 프로필 저장
    @Override
    public UserProfileResponseDTO inputProfileOnce(UserProfileRequestDTO dto) {
        UserEntity user = userRepository.findByKakaoEmail(dto.getKakaoEmail())
                .orElseThrow(() -> new IllegalArgumentException("해당 사용자를 찾을 수 없습니다."));

        // 엔티티 직접 수정
        user.updateProfile(dto.getHeight(), dto.getWeight(), dto.getGender());

        userRepository.save(user);

        return new UserProfileResponseDTO(user.getHeight(), user.getWeight(), user.getGender());
    }

    // 프로필 존재 여부 확인
    @Override
    public UserStatusDTO checkUserStatus(String kakaoEmail) {
        boolean exists = userRepository.existsByKakaoEmail(kakaoEmail);
        int statusCode = exists ? HttpStatus.OK.value() : HttpStatus.CREATED.value();
        return new UserStatusDTO(exists, statusCode);
    }
}
