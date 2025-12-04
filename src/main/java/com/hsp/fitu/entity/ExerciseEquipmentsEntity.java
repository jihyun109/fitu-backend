package com.hsp.fitu.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;

@Getter
@Entity
@Table(name = "exercise_equipments")
public class ExerciseEquipmentsEntity {
    @Id
    private long id;

    @Column(name = "equipment_name")
    private String equipmentName;

    private String equipmentDescription;
}
