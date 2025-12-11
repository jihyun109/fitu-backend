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

    @Column(name = "equipment_id")
    private long equipmentId;

    @Enumerated(EnumType.STRING)
    private Workout name;

    private String workoutName;

    @Column(name = "description")
    private String workoutDescription;

    @Column(name = "image_id")
    private Long imageId;

    private String imageUrl;

    @Column(name = "gif_id")
    private Long gifId;

    private String gifUrl;
}
