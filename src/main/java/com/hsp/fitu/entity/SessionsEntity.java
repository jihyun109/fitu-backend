package com.hsp.fitu.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "sessions")
public class SessionsEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "user_id")
    private long userId;

    @Column(name = "start_time")
    @CreationTimestamp
    private LocalDateTime startTime;

    @Column(name = "exercise_image_id")
    private long exerciseImageId;
}
