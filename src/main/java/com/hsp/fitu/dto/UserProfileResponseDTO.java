package com.hsp.fitu.dto;

import com.hsp.fitu.entity.enums.Gender;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserProfileResponseDTO {
    private int height;
    private int weight;
    private Gender gender;

}

