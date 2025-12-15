package com.hsp.fitu.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "chat_room_members")
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChatRoomMemberEntity {
    @Id
    private Long id;
    private Long chatRoomId;
    private Long userId;
    private Integer unreadCount;
}
