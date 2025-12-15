package com.hsp.fitu.entity;

import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDateTime;

@Entity
@Table(name = "chat_messages")
@Getter
public class ChatMessageEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long chatRoomId;
    private Long senderId;
    private ChatMessageType messageType;
    private String content;
    private LocalDateTime createdAt;

    public enum ChatMessageType {
        ENTER,
        TALK,
        QUIT
    }
}
