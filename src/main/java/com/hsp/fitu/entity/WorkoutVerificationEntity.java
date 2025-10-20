package com.hsp.fitu.entity;

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
    private Long id;
    private Long userId;
    private Long mediaId;
    private WorkoutVerificationType workoutVerificationType;

    @CreationTimestamp
    private Date date;

    private WorkoutVerificationRequestStatus status;
}
