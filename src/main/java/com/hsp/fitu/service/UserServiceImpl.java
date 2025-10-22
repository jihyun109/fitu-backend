package com.hsp.fitu.service;

import com.hsp.fitu.dto.UserFriendCodeResponseDto;
import com.hsp.fitu.dto.UserInfoRequestDTO;
import com.hsp.fitu.dto.UserProfileImageResponseDto;
import com.hsp.fitu.entity.PhysicalInfoEntity;
import com.hsp.fitu.entity.UserEntity;
import com.hsp.fitu.entity.enums.AccountStatus;
import com.hsp.fitu.repository.PhysicalInfoRepository;
import com.hsp.fitu.repository.UniversityRepository;
import com.hsp.fitu.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PhysicalInfoRepository physicalInfoRepository;
    private final UniversityRepository universityRepository;

    @Override
    @Transactional
    public void saveInfo(Long userId, UserInfoRequestDTO userInfoRequestDTO) {
        UserEntity userEntity = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));

        // friend code 부여
        String friendCode = assignFriendCode();

        // 학교 id get
        Long universityId = findUniversityId(userInfoRequestDTO.getUniversityEmail());

        // user info 저장
        userEntity.updateInfo(userInfoRequestDTO, friendCode, universityId);
        userRepository.save(userEntity);

        // 신체 정보 저장
        PhysicalInfoEntity physicalInfoEntity = PhysicalInfoEntity.builder()
                .userId(userId)
                .weight(userInfoRequestDTO.getWeight())
                .height(userInfoRequestDTO.getHeight())
                .muscle(userInfoRequestDTO.getMuscle())
                .bodyFat(userInfoRequestDTO.getBodyFat())
                .build();
        physicalInfoRepository.save(physicalInfoEntity);

    }

    @Override
    public UserProfileImageResponseDto findUserProfileImageAndVisibility(Long userId) {
        return userRepository.findUserProfileImage(userId);
    }

    @Override
    public UserFriendCodeResponseDto getFriendCode(Long userId) {
        return UserFriendCodeResponseDto.builder()
                .friendCode(userRepository.findFriendCodeById(userId))
                .build();
    }

    @Override
    @Transactional
    public void deactivateUser(Long userId) {
        userRepository.updateAccountStatusById(userId, AccountStatus.DEACTIVATED);
    }

    // friend code 부여
    private String assignFriendCode() {
        // 코드 부여 10회 시도
        for (int i = 0; i < 10; i++) {
            String friendCode = generateFriendCode();

            Long id = userRepository.findIdByFriendCode(friendCode);

            if (id == null) {
                return friendCode;
            }
        }
        return null;
    }

    // 코드 생성
    private String generateFriendCode() {
        char[] POOL =
                "ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnopqrstuvwxyz23456789".toCharArray();

        SecureRandom RNG = new SecureRandom();
        int codeLength = 8;

        // 코드 생성
        char[] buf = new char[codeLength];
        for (int i = 0; i < codeLength; i++) {
            buf[i] = POOL[RNG.nextInt(POOL.length)];
        }

        return new String(buf);
    }

    private Long findUniversityId(String universityEmail) {
        String[] emailArray = universityEmail.split("@");

        return universityRepository.findIdByDomainName(emailArray[1]);
    }
}
