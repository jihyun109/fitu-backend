package com.hsp.fitu.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
@Builder
public class FriendListResponseDTO {
    private List<FriendInfo> friendInfoList;

    @Getter
    @AllArgsConstructor
    public static class FriendInfo {
        private Long userId;
        private String userName;
        private String profileImageUrl;
    }
}
