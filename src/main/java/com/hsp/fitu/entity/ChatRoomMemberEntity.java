package com.hsp.fitu.entity;

import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Table(name = "chat_room_members")
@Getter
public class ChatRoomMemberEntity {
    @Id
    private Long id;
    private Long chatRoomId;
    private Long userId;
    private Integer unreadCount;
}
