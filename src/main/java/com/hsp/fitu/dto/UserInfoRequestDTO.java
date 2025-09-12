package com.hsp.fitu.dto;

import com.hsp.fitu.entity.enums.Gender;
import lombok.Getter;

@Getter
public class UserInfoRequestDTO {
    private String name;
    private String universityEmail;
    private int height;
    private int weight;
    private int muscle;
    private int bodyFat;
    private Gender gender;
}
