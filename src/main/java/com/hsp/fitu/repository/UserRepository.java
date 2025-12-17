package com.hsp.fitu.repository;

import com.hsp.fitu.dto.AdminUserResponseDTO;
import com.hsp.fitu.dto.UserProfileImageResponseDto;
import com.hsp.fitu.entity.UserEntity;
import com.hsp.fitu.entity.enums.AccountStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
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

    // 사용자의 friend code 조회
    @Query("SELECT u.friendCode " +
            "FROM UserEntity u " +
            "WHERE u.id = :userId")
    String findFriendCodeById(@Param("userId") Long userId);

    // 사용자의 계정 상태 수정
    @Modifying
    @Query("UPDATE UserEntity u " +
            "SET u.status = :status " +
            "WHERE u.id = :userId")
    void updateAccountStatusById(Long userId, AccountStatus status);

    //관리자 사용자 이름 검색으로 이름, 학교명 조회
    @Query("""
        SELECT new com.hsp.fitu.dto.AdminUserResponseDTO(
            u.id,
            u.name,
            uni.name
        )
        FROM UserEntity u
        JOIN UniversityEntity uni ON u.universityId = uni.id
        WHERE u.name LIKE %:name%
        """)
    List<AdminUserResponseDTO> findByNameContaining(@Param("name") String name);

    // 사용자 id로 profile img id 조회
    @Query("""
            SELECT u.profileImgId
            FROM UserEntity u
            WHERE u.id = :userId
            """)
    Long findProfileImgIdById(@Param("userId") Long userId);
}