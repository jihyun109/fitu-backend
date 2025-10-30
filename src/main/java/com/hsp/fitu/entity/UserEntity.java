package com.hsp.fitu.entity;

import com.hsp.fitu.dto.UserInfoRequestDTO;
import com.hsp.fitu.entity.enums.Gender;
import com.hsp.fitu.entity.enums.Role;
import com.hsp.fitu.entity.enums.AccountStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Date;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "users")
public class UserEntity {
    @Id
    private long id;

    private String name;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    private String kakaoEmail;

    @Enumerated(EnumType.STRING)
    private Role role;

    private String friendCode;

    @Enumerated(EnumType.STRING)
    private AccountStatus status;

    private LocalDateTime suspendEndAt;  // 계정 정지 종료 날짜

    private Long universityId;

    private String universityEmail;

    private Long profileImgId;

    private boolean profileVisibility;

    public void updateProfile(Gender gender) {
        this.gender = gender;
    }

    public void updateRole(Role role) {
        this.role = role;
    }

    public void updateInfo(UserInfoRequestDTO userInfoRequestDTO, String friendCode, Long universityId) {
        this.name = userInfoRequestDTO.getName();
        this.universityEmail = userInfoRequestDTO.getUniversityEmail();
        this.gender = userInfoRequestDTO.getGender();
        this.friendCode = friendCode;
        this.universityId = universityId;
    }

    public void suspend(LocalDateTime suspendEndAt) {
        this.suspendEndAt = suspendEndAt;
    }

    public void unsuspend() {
        this.suspendEndAt = null;
    }

}