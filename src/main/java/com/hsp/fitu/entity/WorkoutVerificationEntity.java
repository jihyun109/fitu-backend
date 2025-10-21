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
    private long id;
    private long userId;
    private long videoId;

    @Enumerated(EnumType.STRING)
    private WorkoutVerificationType workoutType;

    @CreationTimestamp
    private Date requestDate;

    @Enumerated(EnumType.STRING)
    private WorkoutVerificationRequestStatus status;
}
