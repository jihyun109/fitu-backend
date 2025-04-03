package com.hsp.fitu.entity;

import com.hsp.fitu.entity.enums.Workout;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "workouts")
public class WorkoutEntity {
    @Id
    private long id;
    private long categoryId;
    private Workout name;
}
