package com.hsp.fitu.service;

import com.hsp.fitu.dto.UserInfoRequestDTO;
import com.hsp.fitu.entity.PhysicalInfoEntity;
import com.hsp.fitu.entity.UserEntity;
import com.hsp.fitu.repository.PhysicalInfoRepository;
import com.hsp.fitu.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PhysicalInfoRepository physicalInfoRepository;

    @Override
    @Transactional
    public void saveInfo(Long userId, UserInfoRequestDTO userInfoRequestDTO) {
        UserEntity userEntity = userRepository.findById(userId).orElse(null);

        assert userEntity != null : "User must be loaded before mapping";

        // friend code 부여
        assignFriendCode(userEntity);

        // user info 저장
        userEntity.updateInfo(userInfoRequestDTO);

        // 신체 정보 저장
        PhysicalInfoEntity physicalInfoEntity = PhysicalInfoEntity.builder()
                .weight(userInfoRequestDTO.getWeight())
                .muscle(userInfoRequestDTO.getMuscle())
                .bodyFat(userInfoRequestDTO.getBodyFat())
                .build();
        physicalInfoRepository.save(physicalInfoEntity);
    }

    // friend code 부여
    private void assignFriendCode(UserEntity userEntity) {
        // 코드 부여 10회 시도
        for (int i = 0; i < 10; i++) {
            String friendCode = generateFriendCode();

            try {
                userEntity.assignCode(friendCode);
                userRepository.flush();
                break;
            } catch (DataIntegrityViolationException ignored) {

            }
        }
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
}
