package com.hsp.fitu.repository;

import com.hsp.fitu.entity.FriendshipEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FriendshipRepository extends JpaRepository<FriendshipEntity, Long> {
    boolean existsByUserIdAAndUserIdB(Long idA, Long idB);
}
