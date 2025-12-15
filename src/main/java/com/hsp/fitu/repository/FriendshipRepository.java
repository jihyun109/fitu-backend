package com.hsp.fitu.repository;

import com.hsp.fitu.dto.FriendListResponseDTO;
import com.hsp.fitu.entity.FriendshipEntity;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface FriendshipRepository extends JpaRepository<FriendshipEntity, Long> {
    boolean existsByUserIdAAndUserIdB(Long idA, Long idB);

    @Query("""
            SELECT new com.hsp.fitu.dto.FriendListResponseDTO.FriendInfo(
                        u.id,
                        u.name,
                        m.url
                    )
                    FROM FriendshipEntity f
                    JOIN UserEntity u
                      ON u.id = CASE
                                    WHEN f.userIdA = :userId THEN f.userIdB
                                    ELSE f.userIdA
                                END
                    JOIN MediaFilesEntity m
                              ON m.id = u.profileImgId
                    WHERE f.userIdA = :userId
                       OR f.userIdB = :userId
            """)
    List<FriendListResponseDTO.FriendInfo> findFriends(@Param("userId") Long userId);
}
