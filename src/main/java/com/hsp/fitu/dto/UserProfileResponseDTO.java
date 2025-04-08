package com.hsp.fitu.dto;

import com.hsp.fitu.entity.enums.Gender;


public record UserProfileResponseDTO(int height, int weight, Gender gender) {

}
