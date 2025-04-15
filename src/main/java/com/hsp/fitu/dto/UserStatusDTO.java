package com.hsp.fitu.dto;


import lombok.Getter;
import lombok.Setter;

@Setter
@Getter

public class UserStatusDTO {
    private boolean hasProfile;
    private int statusCode;

    public UserStatusDTO(boolean hasProfile, int statusCode) {
        this.hasProfile = hasProfile;
        this.statusCode = statusCode;
    }

    public boolean isHasProfile() {
        return hasProfile;
    }
    public int getStatusCode() {
        return statusCode;
    }
}
