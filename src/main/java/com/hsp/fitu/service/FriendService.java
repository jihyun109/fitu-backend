package com.hsp.fitu.service;

import com.hsp.fitu.dto.FriendAddRequestDTO;

public interface FriendService {
    void addFriend(FriendAddRequestDTO code, Long userId);
}
