package com.hsp.fitu.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Setter
public class ChatRoom {
    private Long roomId;
    private String roomName;
    private String lastMessage;
    private String imgUrl;
}
