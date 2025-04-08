package com.hsp.fitu.dto;


import lombok.Getter;
import lombok.Setter;

@Setter
@Getter

public class UserStatusDTO {
    private boolean hasProfile;

    public UserStatusDTO(boolean hasProfile) {
        this.hasProfile = hasProfile;
    }

}
