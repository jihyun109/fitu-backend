package com.hsp.fitu.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "session_exercises")
public class SessionExercisesEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "session_id")
    private long sessionId;

    @Column(name = "workout_id")
    private long workoutId;

    @Column(name = "order_index")
    private int orderIndex;
}