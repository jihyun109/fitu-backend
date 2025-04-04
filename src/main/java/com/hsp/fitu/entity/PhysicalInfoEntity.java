package com.hsp.fitu.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDate;

@Entity
@Table(name = "physical_infos")
public class PhysicalInfoEntity {
    @Id
    private long id;
    private long userId;
    private LocalDate recordedAt;
    private int bodyFat;
    private int muscle;
}
