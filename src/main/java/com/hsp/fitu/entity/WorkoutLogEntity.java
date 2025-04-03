package com.hsp.fitu.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDate;

@Entity
@Table(name = "workout_logs")
public class WorkoutLogEntity {
    @Id
    private long id;
    private long userId;
    private LocalDate recordedAt;
}
