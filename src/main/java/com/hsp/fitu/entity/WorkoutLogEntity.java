package com.hsp.fitu.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

import java.time.LocalDate;

@Entity
public class WorkoutLogEntity {
    @Id
    private long id;
    private long userId;
    private LocalDate recordedAt;
}
