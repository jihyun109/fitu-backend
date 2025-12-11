package com.hsp.fitu.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "sessions")
public class SessionsEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "user_id")
    private long userId;

    @Column(name = "start_time")
    private LocalDateTime startTime;

    @Setter
    @Column(name = "end_time")
    private LocalDateTime endTime;

    @Setter
    @Column(name = "exercise_image_id")
    private Long exerciseImageId;

}
