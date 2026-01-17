package com.hsp.fitu.service;

import com.hsp.fitu.dto.FriendAddRequestDTO;
import com.hsp.fitu.dto.FriendListResponseDTO;
import com.hsp.fitu.entity.FriendshipEntity;
import com.hsp.fitu.error.BusinessException;
import com.hsp.fitu.error.ErrorCode;
import com.hsp.fitu.repository.FriendshipRepository;
import com.hsp.fitu.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class FriendServiceImpl implements FriendService {
    private final UserRepository userRepository;
    private final FriendshipRepository friendshipRepository;

    @Override
    @Transactional
    public void addFriend(FriendAddRequestDTO dto, Long userId) {
        // code로 사용자 id 검색
        String code = dto.getCode();
        Long userIdToAdd = userRepository.findIdByFriendCode(code).orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        Long userIdA = Math.min(userId, userIdToAdd);
        Long userIdB = Math.max(userId, userIdToAdd);

        // 친구 추가 가능 여부 검증
        validFriendRequest(userIdToAdd, userId, userIdA, userIdB);

        // 친구 관계 저장
        saveFriendship(userIdA, userIdB);
    }

    @Override
    public FriendListResponseDTO getFriends(Long userId) {
        return FriendListResponseDTO.builder()
                .friendInfoList(friendshipRepository.findFriends(userId))
                .build();
    }

    private void validFriendRequest(Long userIdToAdd, Long userId, Long userIdA, Long userIdB) {
        // 해당되는 사용자가 없을 경우
        if (userIdToAdd == null) {
            throw new BusinessException(ErrorCode.INVALID_FRIEND_CODE);
        }

        // 자기 자신을 친구로 추가하려는 경우
        if (userIdToAdd.equals(userId)) {
            throw new BusinessException(ErrorCode.INVALID_FRIEND_REQUEST);
        }

        // 이미 친구인 경우
        if (friendshipRepository.existsByUserIdAAndUserIdB(userIdA, userIdB)) {
            throw new BusinessException(ErrorCode.FRIENDSHIP_ALREADY_EXISTS);
        }
    }

    private void saveFriendship(Long userIdA, Long userIdB) {
        friendshipRepository.save(FriendshipEntity.builder()
                .userIdA(userIdA)
                .userIdB(userIdB)
                .build());
    }
}
