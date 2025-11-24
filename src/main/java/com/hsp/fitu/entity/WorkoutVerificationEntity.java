package com.hsp.fitu.entity;

import com.hsp.fitu.entity.enums.WorkoutVerificationRequestStatus;
import com.hsp.fitu.entity.enums.WorkoutVerificationType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.util.Date;

@Entity
@Table(name = "workout_verifications")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkoutVerificationEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long userId;
    private Long videoId;

    @Enumerated(EnumType.STRING)
    private WorkoutVerificationType workoutType;

    @CreationTimestamp
    private Date requestDate;

    @Enumerated(EnumType.STRING)
    private WorkoutVerificationRequestStatus status;

    private int weight; // 운동 무게

    public void accepted(int weight) {
        this.status= WorkoutVerificationRequestStatus.ACCEPTED;
        this.weight = weight;
    }
}
