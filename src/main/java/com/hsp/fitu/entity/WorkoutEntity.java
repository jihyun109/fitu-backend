package com.hsp.fitu.entity;

import com.hsp.fitu.entity.enums.Workout;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class WorkoutEntity {
    @Id
    private long id;
    private long categoryId;
    private Workout name;
}
