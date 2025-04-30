package com.hsp.fitu.dto;

import com.hsp.fitu.entity.enums.Gender;
import com.hsp.fitu.entity.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;


@Getter
@AllArgsConstructor

public class UserProfileRequestDTO {
    private int height;
    private Gender gender;
    private Role role;
}