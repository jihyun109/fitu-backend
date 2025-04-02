package com.hsp.fitu.entity;

import com.hsp.fitu.entity.enums.WorkoutCategory;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class WorkoutCategoryEntity {
    @Id
    private long id;
    private WorkoutCategory name;

}
