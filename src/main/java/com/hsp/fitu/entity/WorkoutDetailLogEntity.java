package com.hsp.fitu.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Entity
@Table(name = "workout_detail_logs")
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class WorkoutDetailLogEntity {
    @Id
    private long id;
    private long workoutLogId;
    private long workoutId;
    private int weight;
    private int numOfSets;  // 세트 수
    private int repsPerSet; // 세트 당 횟수


}
