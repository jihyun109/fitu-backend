package com.hsp.fitu.entity;

import com.hsp.fitu.entity.enums.WorkoutCategory;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "workout_categories")
public class WorkoutCategoryEntity {
    @Id
    private long id;
    private WorkoutCategory name;

}
