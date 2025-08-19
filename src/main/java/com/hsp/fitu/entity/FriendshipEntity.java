package com.hsp.fitu.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "friendships")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FriendshipEntity {
    @Id
    private Long id;
    private Long userIdA;   // 둘 중 더 작은 ID
    private Long userIdB;
}
