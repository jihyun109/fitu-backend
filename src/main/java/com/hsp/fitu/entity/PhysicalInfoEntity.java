package com.hsp.fitu.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "physical_infos")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class PhysicalInfoEntity {
    @Id
    private long id;
    private long userId;
    private LocalDateTime recordedAt;
    private int bodyFat;
    private int muscle;
    private int height;
    private int weight;

    @PrePersist
    protected void onCreate() {
        this.recordedAt = LocalDateTime.now();
    }
}
