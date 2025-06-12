package com.hsp.fitu.service;

import com.hsp.fitu.dto.RoutineRecommendationRequestDTO;
import com.hsp.fitu.dto.RoutineRecommendationResponseDTO;
import com.hsp.fitu.dto.WorkoutWithImageDTO;
import com.hsp.fitu.entity.WorkoutCategoryEntity;
import com.hsp.fitu.entity.WorkoutEntity;
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

        Set<Workout> selectedMainWorkouts = new HashSet<>();
        List<RoutineRecommendationResponseDTO> responseList = new ArrayList<>();

        for (WorkoutCategoryEntity category : sortedCategories) {

            WorkoutCategory categoryName = category.getName();
            int count = workoutCountMap.get(categoryName);

            List<WorkoutEntity> allWorkouts = workoutRepository.findAllByCategoryId(category.getId());
            Collections.shuffle(allWorkouts);

            int added = 0;

            // mainWorkout 선정
            for (WorkoutEntity workout : allWorkouts) {
                Workout mainWorkout = workout.getName();
                if (selectedMainWorkouts.contains(mainWorkout)) continue;

                // similar workout 선정
                List<WorkoutEntity> similarCandidates = workoutRepository.findSimilarWorkouts(mainWorkout, category.getId());
                Collections.shuffle(similarCandidates);

                selectedMainWorkouts.add(mainWorkout);

                // response 생성
                String mainImageUrl = workout.getImageUrl();

                List<WorkoutWithImageDTO> similarList = similarCandidates.stream()
                        .filter(alt -> !alt.equals(workout))
                        .limit(4)
                        .map(alt -> WorkoutWithImageDTO.builder()
                                .workout(alt.getName())
                                .imageUrl(alt.getImageUrl()) // 이미지 URL 조회
                                .build())
                        .toList();

                responseList.add(RoutineRecommendationResponseDTO.builder()
                        .mainWorkout(WorkoutWithImageDTO.builder()
                                .workout(mainWorkout)
                                .imageUrl(mainImageUrl).build())
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
