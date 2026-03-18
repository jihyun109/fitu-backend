package com.hsp.fitu.repository;

import com.hsp.fitu.dto.ChatRoom;
import com.hsp.fitu.entity.*;
import jakarta.persistence.EntityManager;
import org.hibernate.Session;
import org.hibernate.stat.Statistics;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class ChatRoomRepositoryTest {

    @Autowired
    private ChatRoomRepository chatRoomRepository;

    @Autowired
    private EntityManager em;

    private Long myUserId;
    private Long otherUserId;
    private Long chatRoomId;

    @BeforeEach
    void setUp() {
        // 상대방 프로필 이미지
        MediaFilesEntity otherProfileImg = MediaFilesEntity.builder()
                .url("https://example.com/other-profile.jpg")
                .build();
        em.persist(otherProfileImg);

        // 채팅방 썸네일 이미지 (변경 전에 사용되던 것)
        MediaFilesEntity roomThumbnail = MediaFilesEntity.builder()
                .url("https://example.com/room-thumbnail.jpg")
                .build();
        em.persist(roomThumbnail);

        // 나 (요청자) - UserEntity의 id는 @GeneratedValue가 아니므로 직접 설정
        UserEntity me = UserEntity.builder()
                .id(1L)
                .name("나")
                .profileImgId(null)
                .build();
        em.persist(me);
        myUserId = me.getId();

        // 상대방
        UserEntity other = UserEntity.builder()
                .id(2L)
                .name("상대방닉네임")
                .profileImgId(otherProfileImg.getId())
                .build();
        em.persist(other);
        otherUserId = other.getId();

        // 채팅방 생성
        ChatRoomEntity chatRoom = ChatRoomEntity.builder()
                .roomName("원래채팅방이름")
                .thumbnailImgId(roomThumbnail.getId())
                .build();
        em.persist(chatRoom);
        chatRoomId = chatRoom.getId();

        // 채팅방 멤버 등록 (나 + 상대방)
        em.persist(ChatRoomMemberEntity.builder()
                .chatRoomId(chatRoomId)
                .userId(myUserId)
                .build());
        em.persist(ChatRoomMemberEntity.builder()
                .chatRoomId(chatRoomId)
                .userId(otherUserId)
                .build());

        em.flush();
        em.clear();
    }

    @Test
    @DisplayName("채팅방 목록 조회 시 roomName에 상대방 이름이 표시된다")
    void getChatRoomList_shouldShowOtherUserName() {
        // when
        List<ChatRoom> result = chatRoomRepository.getChatRoomList(myUserId);

        // then
        assertThat(result).hasSize(1);
        ChatRoom chatRoom = result.get(0);

        // 핵심 검증: roomName이 "원래채팅방이름"이 아니라 상대방 이름 "상대방닉네임"
        assertThat(chatRoom.getRoomName()).isEqualTo("상대방닉네임");
    }

    @Test
    @DisplayName("채팅방 목록 조회 시 썸네일에 상대방 프로필 이미지가 표시된다")
    void getChatRoomList_shouldShowOtherUserProfileImg() {
        // when
        List<ChatRoom> result = chatRoomRepository.getChatRoomList(myUserId);

        // then
        assertThat(result).hasSize(1);
        ChatRoom chatRoom = result.get(0);

        // 핵심 검증: imgUrl이 채팅방 썸네일("room-thumbnail.jpg")이 아니라
        //           상대방 프로필 이미지("other-profile.jpg")
        assertThat(chatRoom.getImgUrl())
                .isEqualTo("https://example.com/other-profile.jpg");
    }

    @Test
    @DisplayName("상대방 프로필 이미지가 없으면 imgUrl은 null이다")
    void getChatRoomList_noProfileImg_shouldReturnNullImgUrl() {
        // given - 프로필 이미지 없는 유저와의 새 채팅방
        UserEntity noImgUser = UserEntity.builder()
                .id(3L)
                .name("이미지없는유저")
                .profileImgId(null)
                .build();
        em.persist(noImgUser);

        ChatRoomEntity chatRoom2 = ChatRoomEntity.builder()
                .roomName("두번째채팅방")
                .build();
        em.persist(chatRoom2);

        em.persist(ChatRoomMemberEntity.builder()
                .chatRoomId(chatRoom2.getId())
                .userId(myUserId)
                .build());
        em.persist(ChatRoomMemberEntity.builder()
                .chatRoomId(chatRoom2.getId())
                .userId(noImgUser.getId())
                .build());
        em.flush();
        em.clear();

        // when
        List<ChatRoom> result = chatRoomRepository.getChatRoomList(myUserId);

        // then
        ChatRoom noImgChatRoom = result.stream()
                .filter(cr -> cr.getRoomName().equals("이미지없는유저"))
                .findFirst()
                .orElseThrow();

        assertThat(noImgChatRoom.getImgUrl()).isNull();
    }

    @Test
    @DisplayName("상대방 관점에서 조회하면 나의 이름이 roomName에 표시된다")
    void getChatRoomList_fromOtherUserPerspective_shouldShowMyName() {
        // when - 상대방이 채팅방 목록을 조회
        List<ChatRoom> result = chatRoomRepository.getChatRoomList(otherUserId);

        // then - 상대방에게는 내 이름("나")이 표시되어야 함
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getRoomName()).isEqualTo("나");
    }

    @Test
    @DisplayName("마지막 메시지가 올바르게 조회된다")
    void getChatRoomList_shouldShowLastMessage() {
        // given - 채팅 메시지 2건 추가
        em.persist(ChatMessageEntity.builder()
                .chatRoomId(chatRoomId)
                .senderId(myUserId)
                .content("첫번째 메시지")
                .build());

        // 약간의 시간 차이를 두기 위해 직접 createdAt 설정이 필요하지만
        // @PrePersist로 자동 설정되므로, 별도 메시지를 추가
        em.persist(ChatMessageEntity.builder()
                .chatRoomId(chatRoomId)
                .senderId(otherUserId)
                .content("마지막 메시지")
                .build());

        em.flush();
        em.clear();

        // when
        List<ChatRoom> result = chatRoomRepository.getChatRoomList(myUserId);

        // then - 마지막 메시지가 조회됨
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getLastMessage()).isNotNull();
    }

    @Test
    @DisplayName("채팅방 10개를 단 1번의 쿼리로 조회한다 (N+1 제거 검증)")
    void getChatRoomList_shouldExecuteSingleQuery_notNPlusOne() {
        // given - 채팅방 10개 생성 (setUp에서 1개 + 여기서 9개 추가)
        for (int i = 3; i <= 11; i++) {
            UserEntity user = UserEntity.builder()
                    .id((long) i)
                    .name("상대방" + i)
                    .build();
            em.persist(user);

            ChatRoomEntity room = ChatRoomEntity.builder()
                    .roomName("채팅방" + i)
                    .build();
            em.persist(room);

            em.persist(ChatRoomMemberEntity.builder()
                    .chatRoomId(room.getId())
                    .userId(myUserId)
                    .build());
            em.persist(ChatRoomMemberEntity.builder()
                    .chatRoomId(room.getId())
                    .userId(user.getId())
                    .build());
        }
        em.flush();
        em.clear();

        // Hibernate Statistics로 쿼리 수 측정
        Statistics stats = em.unwrap(Session.class).getSessionFactory().getStatistics();
        stats.setStatisticsEnabled(true);
        stats.clear();

        // when
        List<ChatRoom> result = chatRoomRepository.getChatRoomList(myUserId);

        // then
        long queryCount = stats.getQueryExecutionCount();

        assertThat(result).hasSize(10); // 총 10개 채팅방

        // 핵심 검증: 채팅방이 몇 개든 쿼리는 딱 1번만 실행
        // 변경 전(N+1): 1(목록 조회) + 10(각 채팅방 이미지 조회) = 11번
        // 변경 후:       1(단일 JOIN 쿼리) = 1번
        assertThat(queryCount)
                .as("변경 전이라면 11번 실행됐을 쿼리가, 변경 후에는 1번만 실행되어야 한다")
                .isEqualTo(1L);

        System.out.println("========================================");
        System.out.println("  채팅방 수: " + result.size() + "개");
        System.out.println("  실행된 쿼리 수: " + queryCount + "회");
        System.out.println("  변경 전이었다면: " + (result.size() + 1) + "회 (N+1)");
        System.out.println("  절감률: " + String.format("%.0f%%", (1.0 - (double) queryCount / (result.size() + 1)) * 100));
        System.out.println("========================================");

        stats.setStatisticsEnabled(false);
    }

    @Test
    @DisplayName("내가 속하지 않은 채팅방은 조회되지 않는다")
    void getChatRoomList_shouldNotShowUnrelatedChatRooms() {
        // given - 나와 관계없는 채팅방
        UserEntity user3 = UserEntity.builder()
                .id(3L)
                .name("유저3")
                .build();
        UserEntity user4 = UserEntity.builder()
                .id(4L)
                .name("유저4")
                .build();
        em.persist(user3);
        em.persist(user4);

        ChatRoomEntity otherRoom = ChatRoomEntity.builder()
                .roomName("다른사람들의채팅방")
                .build();
        em.persist(otherRoom);

        em.persist(ChatRoomMemberEntity.builder()
                .chatRoomId(otherRoom.getId())
                .userId(user3.getId())
                .build());
        em.persist(ChatRoomMemberEntity.builder()
                .chatRoomId(otherRoom.getId())
                .userId(user4.getId())
                .build());
        em.flush();
        em.clear();

        // when
        List<ChatRoom> result = chatRoomRepository.getChatRoomList(myUserId);

        // then - 내 채팅방 1개만 조회됨
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getRoomId()).isEqualTo(chatRoomId);
    }
}
