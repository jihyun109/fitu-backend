package com.hsp.fitu.repository;

import com.hsp.fitu.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, Long> {
    Optional<UserEntity> findByKakaoEmail(String kakaoEmail);
    Optional<UserEntity> findById(Long id);

    // 친구 초대 코드로 사용자 id 조회
    @Query("SELECT u.id FROM UserEntity u WHERE u.friendCode = :code")
    Long findIdByFriendCode(@Param("code") String code);
}