package com.hsp.fitu.service;

import com.hsp.fitu.dto.ChatRoom;
import com.hsp.fitu.dto.ChatRoomListResponseDTO;
import com.hsp.fitu.repository.ChatRoomMemberRepository;
import com.hsp.fitu.repository.ChatRoomRepository;
import com.hsp.fitu.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ChatRoomServiceImplTest {

    @Mock
    private ChatRoomRepository chatRoomRepository;

    @Mock
    private ChatRoomMemberRepository chatRoomMemberRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ChatRoomServiceImpl chatRoomService;

    @Test
    @DisplayName("getChatRoomList는 repository에서 반환된 결과를 그대로 반환한다 (추가 쿼리 없음)")
    void getChatRoomList_returnsResultDirectly_noAdditionalQuery() {
        // given
        Long userId = 1L;
        List<ChatRoom> mockChatRooms = List.of(
                new ChatRoom(100L, "상대방이름", "안녕하세요", "https://example.com/profile.jpg"),
                new ChatRoom(200L, "다른상대방", "반갑습니다", "https://example.com/other.jpg")
        );
        when(chatRoomRepository.getChatRoomList(userId)).thenReturn(mockChatRooms);

        // when
        ChatRoomListResponseDTO result = chatRoomService.getChatRoomList(userId);

        // then - 결과가 올바르게 반환됨
        assertThat(result.getChatRoomList()).hasSize(2);
        assertThat(result.getChatRoomList().get(0).getRoomName()).isEqualTo("상대방이름");
        assertThat(result.getChatRoomList().get(0).getImgUrl()).isEqualTo("https://example.com/profile.jpg");
        assertThat(result.getChatRoomList().get(1).getRoomName()).isEqualTo("다른상대방");

        // 핵심 검증: getChatRoomList만 1번 호출되고, 추가 쿼리가 없음 (N+1 제거)
        verify(chatRoomRepository, times(1)).getChatRoomList(userId);
        verifyNoMoreInteractions(chatRoomRepository);
    }

    @Test
    @DisplayName("채팅방이 없으면 빈 리스트를 반환한다")
    void getChatRoomList_noChatRooms_returnsEmptyList() {
        // given
        Long userId = 1L;
        when(chatRoomRepository.getChatRoomList(userId)).thenReturn(List.of());

        // when
        ChatRoomListResponseDTO result = chatRoomService.getChatRoomList(userId);

        // then
        assertThat(result.getChatRoomList()).isEmpty();
        verify(chatRoomRepository, times(1)).getChatRoomList(userId);
        verifyNoMoreInteractions(chatRoomRepository);
    }
}
