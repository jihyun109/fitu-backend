package com.hsp.fitu.service;

import com.hsp.fitu.repository.projection.WorkoutCategoryNameOnly;

import java.util.List;

public interface WorkoutCategoryService {
    List<WorkoutCategoryNameOnly> getWorkoutCategories();
}
