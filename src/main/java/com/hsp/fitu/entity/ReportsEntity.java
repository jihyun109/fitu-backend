package com.hsp.fitu.entity;

import com.hsp.fitu.entity.enums.TargetType;
import jakarta.persistence.*;
import lombok.Getter;

import java.util.Date;


@Getter
@Entity
@Table(name = "reports")
public class ReportsEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "reporter_id")
    private long reporterId;

    @Column(name = "target_id")
    private long targetId;

    @Column(name = "target_type")
    @Enumerated(EnumType.STRING)
    private TargetType targetType;

    @Column(name = "created_at")
    private Date recordedAt;
}
