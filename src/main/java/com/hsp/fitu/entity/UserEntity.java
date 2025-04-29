package com.hsp.fitu.entity;

import com.hsp.fitu.entity.enums.Gender;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "users")
public class UserEntity {
    @Id
    private long id;
    private String kakaoEmail;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    public void updateProfile( Gender gender) {
        this.gender = gender;

    }
}