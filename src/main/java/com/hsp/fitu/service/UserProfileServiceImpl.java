package com.hsp.fitu.service;

import com.hsp.fitu.dto.UserProfileRequestDTO;
import com.hsp.fitu.entity.UserEntity;
import com.hsp.fitu.entity.PhysicalInfoEntity;
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

    // 프로필 저장
    @Override
    public void inputProfileOnce(long userId, UserProfileRequestDTO dto) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("해당 사용자를 찾을 수 없습니다."));

        // UserEntity(gender) 수정
        user.updateRole(dto.getRole());
        user.updateProfile(dto.getGender());

        // PhysicalInfoEntity(height, weight) 업데이트
        PhysicalInfoEntity entity = PhysicalInfoEntity.builder()
                .userId(userId)
                .height(dto.getHeight())
                .build();

        physicalInfoRepository.save(entity);
    }
}
