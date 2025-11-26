package com.hsp.fitu.service;

import com.hsp.fitu.entity.ExerciseEquipmentsEntity;
import com.hsp.fitu.repository.AdminAddExerciseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminAddExerciseServiceImpl implements AdminAddExerciseService{
    private final AdminAddExerciseRepository adminAddExerciseRepository;

    @Override
    public ExerciseEquipmentsEntity getEquipmentById(long id) {
        return adminAddExerciseRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Equipment not found"));
    }

    @Override
    public List<ExerciseEquipmentsEntity> getAllEquipments() {
        return adminAddExerciseRepository.findAll();
    }
}
