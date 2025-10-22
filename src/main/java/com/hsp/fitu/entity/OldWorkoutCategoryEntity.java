package com.hsp.fitu.entity;

import com.hsp.fitu.entity.enums.WorkoutCategory;
import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Table(name = "workout_categories")
@Getter
public class OldWorkoutCategoryEntity {
    @Id
    private long id;
    @Enumerated(EnumType.STRING)
    private WorkoutCategory name;
    private int priority;
}
