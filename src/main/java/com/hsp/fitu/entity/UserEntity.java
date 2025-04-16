package com.hsp.fitu.entity;

import com.hsp.fitu.entity.enums.Gender;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
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
    private Gender gender;

    public void updateProfile(int height, int weight, Gender gender) {
        this.height = height;
        this.weight = weight;
        this.gender = gender;

    }
}