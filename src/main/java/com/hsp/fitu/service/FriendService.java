package com.hsp.fitu.service;

import com.hsp.fitu.dto.FriendAddRequestDTO;
import com.hsp.fitu.dto.FriendListResponseDTO;

public interface FriendService {
    void addFriend(FriendAddRequestDTO code, Long userId);

    FriendListResponseDTO getFriends(Long userId);
}
