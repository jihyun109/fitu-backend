package com.hsp.fitu.entity;

import com.hsp.fitu.entity.enums.Workout;
import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Getter
@Table(name = "workouts")
public class WorkoutEntity {
    @Id
    private long id;
    private long categoryId;

    @Enumerated(EnumType.STRING)
    private Workout name;
    private String imageUrl;
    private String gifUrl;
}
