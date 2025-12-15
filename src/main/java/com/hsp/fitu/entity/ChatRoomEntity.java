package com.hsp.fitu.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Table(name = "chat_rooms")
@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatRoomEntity {
    @Id
    private Long id;
    private String roomName;
}
