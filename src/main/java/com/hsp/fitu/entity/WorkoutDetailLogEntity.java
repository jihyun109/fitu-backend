package com.hsp.fitu.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class WorkoutDetailLogEntity {
    @Id
    private long id;
    private long workoutLogId;
    private long workoutId;
    private int weight;
    private int numOfSets;  // 세트 수
    private int repsPerSet; // 세트 당 횟수
}
