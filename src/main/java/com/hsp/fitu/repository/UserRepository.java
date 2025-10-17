package com.hsp.fitu.repository;

import com.hsp.fitu.dto.UserProfileImageResponseDto;
import com.hsp.fitu.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, Long> {
    // 사용자 이름 조회
    @Query("SELECT u.name " +
            "FROM UserEntity u " +
            "WHERE u.id = :userId")
    String findNameById(@Param("userId") Long userId);

    Optional<UserEntity> findByKakaoEmail(String kakaoEmail);

    // 친구 초대 코드로 사용자 id 조회
    @Query("SELECT u.id FROM UserEntity u WHERE u.friendCode = :code")
    Long findIdByFriendCode(@Param("code") String code);

    // 사용자 프로필 사진과 공유 여부 조회
    @Query("SELECT new com.hsp.fitu.dto.UserProfileImageResponseDto(m.url, u.profileVisibility) " +
            "FROM UserEntity u, MediaFilesEntity m " +
            "WHERE u.profileImgId = m.id " +
            "AND u.id = :userId")
    UserProfileImageResponseDto findUserProfileImage(@Param("userId") long userId);
}