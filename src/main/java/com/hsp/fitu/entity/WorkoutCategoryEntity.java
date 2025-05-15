package com.hsp.fitu.entity;

import com.hsp.fitu.entity.enums.WorkoutCategory;
import jakarta.persistence.*;

@Entity
@Table(name = "workout_categories")
public class WorkoutCategoryEntity {
    @Id
    private long id;
    @Enumerated(EnumType.STRING)
    private WorkoutCategory name;
    private int priority;
}
