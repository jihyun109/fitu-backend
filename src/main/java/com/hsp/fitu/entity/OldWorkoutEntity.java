package com.hsp.fitu.entity;

import com.hsp.fitu.entity.enums.Workout;
import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Getter
@Table(name = "workouts")
public class OldWorkoutEntity {
    @Id
    private long id;
    private long categoryId;

    @Enumerated(EnumType.STRING)
    private Workout name;
    private String imageUrl;
    private String gifUrl;

    public void updateImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void updateGifUrl(String gifUrl) {
        this.gifUrl = gifUrl;
    }
}
