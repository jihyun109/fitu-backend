package com.hsp.fitu.service;

import com.hsp.fitu.dto.FriendAddRequestDTO;
import com.hsp.fitu.entity.FriendshipEntity;
import com.hsp.fitu.error.BusinessException;
import com.hsp.fitu.error.ErrorCode;
import com.hsp.fitu.repository.FriendshipRepository;
import com.hsp.fitu.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FriendServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private FriendshipRepository friendshipRepository;

    @InjectMocks
    private FriendServiceImpl friendService;

    @Test
    @DisplayName("유효한 친구 코드로 친구 추가 시 친구 관계가 저장된다")
    void addFriend_validCode_savesFriendship() {
        // given
        Long myId = 1L;
        Long friendId = 2L;
        FriendAddRequestDTO dto = mock(FriendAddRequestDTO.class);
        when(dto.getCode()).thenReturn("FRIEND001");
        when(userRepository.findIdByFriendCode("FRIEND001")).thenReturn(Optional.of(friendId));
        when(friendshipRepository.existsByUserIdAAndUserIdB(myId, friendId)).thenReturn(false);

        // when
        friendService.addFriend(dto, myId);

        // then
        verify(friendshipRepository).save(any(FriendshipEntity.class));
    }

    @Test
    @DisplayName("존재하지 않는 친구 코드로 추가 시 USER_NOT_FOUND 예외가 발생한다")
    void addFriend_invalidCode_throwsUserNotFound() {
        // given
        FriendAddRequestDTO dto = mock(FriendAddRequestDTO.class);
        when(dto.getCode()).thenReturn("INVALID");
        when(userRepository.findIdByFriendCode("INVALID")).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> friendService.addFriend(dto, 1L))
                .isInstanceOf(BusinessException.class)
                .extracting(e -> ((BusinessException) e).getErrorCode())
                .isEqualTo(ErrorCode.USER_NOT_FOUND);
    }

    @Test
    @DisplayName("자기 자신을 친구로 추가하면 INVALID_FRIEND_REQUEST 예외가 발생한다")
    void addFriend_selfAdd_throwsInvalidFriendRequest() {
        // given
        Long myId = 1L;
        FriendAddRequestDTO dto = mock(FriendAddRequestDTO.class);
        when(dto.getCode()).thenReturn("MY_CODE");
        when(userRepository.findIdByFriendCode("MY_CODE")).thenReturn(Optional.of(myId)); // 자기 자신 ID 반환

        // when & then
        assertThatThrownBy(() -> friendService.addFriend(dto, myId))
                .isInstanceOf(BusinessException.class)
                .extracting(e -> ((BusinessException) e).getErrorCode())
                .isEqualTo(ErrorCode.INVALID_FRIEND_REQUEST);
    }

    @Test
    @DisplayName("이미 친구인 경우 FRIENDSHIP_ALREADY_EXISTS 예외가 발생한다")
    void addFriend_alreadyFriends_throwsFriendshipAlreadyExists() {
        // given
        Long myId = 1L;
        Long friendId = 2L;
        FriendAddRequestDTO dto = mock(FriendAddRequestDTO.class);
        when(dto.getCode()).thenReturn("FRIEND001");
        when(userRepository.findIdByFriendCode("FRIEND001")).thenReturn(Optional.of(friendId));
        when(friendshipRepository.existsByUserIdAAndUserIdB(myId, friendId)).thenReturn(true);

        // when & then
        assertThatThrownBy(() -> friendService.addFriend(dto, myId))
                .isInstanceOf(BusinessException.class)
                .extracting(e -> ((BusinessException) e).getErrorCode())
                .isEqualTo(ErrorCode.FRIENDSHIP_ALREADY_EXISTS);
    }

    @Test
    @DisplayName("요청자 ID가 대상 ID보다 크더라도 항상 작은 ID가 userIdA로 저장된다")
    void addFriend_idOrdering_smallerIdIsAlwaysUserIdA() {
        // given - 요청자(5L)가 대상(2L)보다 ID가 크다
        Long myId = 5L;
        Long friendId = 2L;
        FriendAddRequestDTO dto = mock(FriendAddRequestDTO.class);
        when(dto.getCode()).thenReturn("FRIEND002");
        when(userRepository.findIdByFriendCode("FRIEND002")).thenReturn(Optional.of(friendId));
        when(friendshipRepository.existsByUserIdAAndUserIdB(friendId, myId)).thenReturn(false);

        ArgumentCaptor<FriendshipEntity> captor = ArgumentCaptor.forClass(FriendshipEntity.class);

        // when
        friendService.addFriend(dto, myId);

        // then - ID 순서와 상관없이 항상 작은 ID가 A, 큰 ID가 B
        verify(friendshipRepository).save(captor.capture());
        FriendshipEntity saved = captor.getValue();
        assertThat(saved.getUserIdA()).isEqualTo(2L);
        assertThat(saved.getUserIdB()).isEqualTo(5L);
    }
}
