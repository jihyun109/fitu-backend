package com.hsp.fitu.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;

@Table(name = "chat_rooms")
@Entity
@Getter
public class ChatRoomEntity {
    @Id
    private Long id;
    private String roomName;
}
