package com.hsp.fitu.dto;

import com.hsp.fitu.entity.enums.Gender;
import lombok.AllArgsConstructor;
import lombok.Getter;


@Getter
@AllArgsConstructor

public class UserProfileRequestDTO {
    private String kakaoEmail;
    private int height;
    private int weight;
    private Gender gender;

}
