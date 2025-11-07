package com.hsp.fitu.entity;

import com.hsp.fitu.entity.enums.TargetType;
import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Entity
@Table(name = "reports")
public class ReportsEntity {
    @Id
    private long id;

    @Column(name = "reporter_id")
    private long reporterId;

    @Column(name = "target_id")
    private long targetId;

    @Column(name = "target_type")
    @Enumerated(EnumType.STRING)
    private TargetType targetType;

    private LocalDateTime recordedAt;
}
