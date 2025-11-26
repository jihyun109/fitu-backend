package com.hsp.fitu.service;

import com.hsp.fitu.entity.ExerciseEquipmentsEntity;

import java.util.List;

public interface AdminAddExerciseService {
    ExerciseEquipmentsEntity getEquipmentById(long id);

    List<ExerciseEquipmentsEntity> getAllEquipments();
}
