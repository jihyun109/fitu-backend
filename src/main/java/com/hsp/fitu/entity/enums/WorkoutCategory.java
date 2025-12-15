package com.hsp.fitu.entity.enums;

public enum WorkoutCategory {
    SHOULDER("어깨"),
    CHEST("가슴"),
    BACK("등"),
    ARM("팔"),
    LOWER_PART("하체");

    private final String korean;

    WorkoutCategory(String korean) {
        this.korean = korean;
    }

    public String getKorean() {
        return korean;
    }
}
