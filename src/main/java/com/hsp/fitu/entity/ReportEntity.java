package com.hsp.fitu.entity;

import com.hsp.fitu.entity.enums.TargetType;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
@Table(name ="reports")
public class ReportEntity {
    @Id
    private Long id;
    private Long reporterId;
    private Long targetId;
    private TargetType targetType;
}
