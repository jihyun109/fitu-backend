package com.hsp.fitu.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class FriendInfo {
    private Long userId;
    private String userName;
    private String profileImageUrl;
}
