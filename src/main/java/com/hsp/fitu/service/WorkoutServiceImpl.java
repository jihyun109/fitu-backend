package com.hsp.fitu.service;

import com.hsp.fitu.dto.RoutineRecommendationRequestDTO;
import com.hsp.fitu.dto.RoutineRecommendationResponseDTO;
import com.hsp.fitu.entity.WorkoutCategoryEntity;
import com.hsp.fitu.entity.enums.Workout;
import com.hsp.fitu.entity.enums.WorkoutCategory;
import com.hsp.fitu.repository.WorkoutCategoryRepository;
import com.hsp.fitu.repository.WorkoutRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class WorkoutServiceImpl implements WorkoutService {
    private final WorkoutRepository workoutRepository;
    private final WorkoutCategoryRepository workoutCategoryRepository;

    @Override
    public List<RoutineRecommendationResponseDTO> suggestRoutine(RoutineRecommendationRequestDTO requestDTO) {
        List<WorkoutCategoryEntity> sortedCategories =
                workoutCategoryRepository.findByNameInOrderByPriority(requestDTO.getWorkoutCategoryList());

        Map<WorkoutCategory, Integer> workoutCountMap = allocateWorkoutCounts(sortedCategories);
        log.info(workoutCountMap.toString());

        Set<Workout> selectedMainWorkouts = new HashSet<>();
        List<RoutineRecommendationResponseDTO> responseList = new ArrayList<>();

        for (WorkoutCategoryEntity category : sortedCategories) {

            WorkoutCategory categoryName = category.getName();
            int count = workoutCountMap.get(categoryName);

            List<Workout> allWorkouts = workoutRepository.findNamesByCategory(category.getId());
            Collections.shuffle(allWorkouts);

            int added = 0;

            for (Workout workout : allWorkouts) {
                if (selectedMainWorkouts.contains(workout)) continue;

                // mainWorkout 선정
                List<Workout> similarCandidates = workoutRepository.findSimilarWorkouts(workout, category.getId());
                Collections.shuffle(similarCandidates);

                List<Workout> similarList = similarCandidates.stream()
                        .filter(alt -> !alt.equals(workout))
                        .limit(4)
                        .toList();

                selectedMainWorkouts.add(workout);

                responseList.add(RoutineRecommendationResponseDTO.builder()
                        .mainWorkout(workout)
                        .similarWorkouts(similarList)
                        .build());

                if (++added == count) break;
            }
        }

        return responseList;

    }

    private Map<WorkoutCategory, Integer> allocateWorkoutCounts(List<WorkoutCategoryEntity> sortedCategories) {
        Map<WorkoutCategory, Integer> workoutCountMap = new HashMap<>();
        WorkoutCategory workoutCategory;

        switch (sortedCategories.size()) {
            case 3:
                for (int i = 0; i < 2; i++) {
                    workoutCategory = sortedCategories.get(i).getName();
                    workoutCountMap.put(workoutCategory, 2);
                }

                workoutCategory = sortedCategories.get(2).getName();
                workoutCountMap.put(workoutCategory, 1);
                break;
            case 4:
                workoutCategory = sortedCategories.get(0).getName();
                workoutCountMap.put(workoutCategory, 2);

                for (int i = 1; i < sortedCategories.size(); i++) {
                    workoutCategory = sortedCategories.get(i).getName();
                    workoutCountMap.put(workoutCategory, 1);
                }
                break;
            case 5:
                for (int i = 0; i < sortedCategories.size(); i++) {
                    workoutCategory = sortedCategories.get(i).getName();
                    workoutCountMap.put(workoutCategory, 1);
                }
                break;
        }

        return workoutCountMap;
    }
}
