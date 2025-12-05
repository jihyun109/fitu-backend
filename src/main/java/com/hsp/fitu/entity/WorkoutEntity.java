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

    @Column(name = "image_id")
    private String imageId;

    @Column(name = "gif_id")
    private String gifId;

}
