package com.hsp.fitu.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;

@Getter
@Entity
@Table(name = "university_gym_equipments")
public class UniversityGymEquipmentEntity {
    @Id
    private long id;

    @Column(name = "university_id")
    private long universityId;

    @Column(name = "equipment_id")
    private long equipmentId;
}
