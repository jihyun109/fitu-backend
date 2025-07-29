package com.hsp.fitu.service;

import com.hsp.fitu.dto.AdditionalInfoResponseDTO;
import com.hsp.fitu.dto.UserProfileRequestDTO;
import com.hsp.fitu.entity.UserEntity;
import com.hsp.fitu.entity.PhysicalInfoEntity;
import com.hsp.fitu.entity.enums.Role;
import com.hsp.fitu.error.ErrorCode;
import com.hsp.fitu.error.customExceptions.UserNotFoundException;
import com.hsp.fitu.jwt.JwtUtil;
import com.hsp.fitu.repository.PhysicalInfoRepository;
import com.hsp.fitu.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserProfileServiceImpl implements UserProfileService {

    private final UserRepository userRepository;
    private final PhysicalInfoRepository physicalInfoRepository;
    private final JwtUtil jwtUtil;

    // 프로필 저장
    @Override
    public AdditionalInfoResponseDTO inputProfileOnce(long userId, UserProfileRequestDTO dto) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(ErrorCode.USER_NOT_FOUND));

        // UserEntity(gender) 수정
        Role role = dto.getRole();
        user.updateRole(role);
        user.updateProfile(dto.getGender());

        // PhysicalInfoEntity(height, weight) 업데이트
        PhysicalInfoEntity entity = PhysicalInfoEntity.builder()
                .userId(userId)
                .height(dto.getHeight())
                .build();

        physicalInfoRepository.save(entity);

        // role 이 포함된 토큰 발급
        String newToken = jwtUtil.createAccessToken(userId, role);
        return AdditionalInfoResponseDTO.builder()
                .token(newToken)
                .build();
    }
}