package com.hsp.fitu.dto;

import lombok.Getter;

import java.util.List;

@Getter
public class ChatRoomCreateRequestDTO {
    private String name;
    private List<Long>  memberIds;
}
