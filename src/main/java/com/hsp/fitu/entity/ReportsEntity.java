package com.hsp.fitu.entity;

import com.hsp.fitu.entity.enums.TargetType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
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
}