package com.hsp.fitu.service;

import com.hsp.fitu.repository.WorkoutCategoryRepository;
import com.hsp.fitu.repository.projection.WorkoutCategoryNameOnly;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class WorkoutCategoryServiceImpl implements WorkoutCategoryService{
    private final WorkoutCategoryRepository workoutCategoryRepository;

    @Override
    @Transactional(readOnly = true)
    public List<WorkoutCategoryNameOnly> getWorkoutCategories() {
        workoutCategoryRepository.findAllBy();
        return null;
    }
}
