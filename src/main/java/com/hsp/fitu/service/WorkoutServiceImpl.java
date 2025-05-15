package com.hsp.fitu.service;

import com.hsp.fitu.dto.RoutineRecommendationRequestDTO;
import com.hsp.fitu.dto.RoutineRecommendationResponseDTO;
import com.hsp.fitu.entity.enums.Workout;
import com.hsp.fitu.entity.enums.WorkoutCategory;
import com.hsp.fitu.repository.WorkoutCategoryRepository;
import com.hsp.fitu.repository.WorkoutRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class WorkoutServiceImpl implements WorkoutService {
    private final WorkoutRepository workoutRepository;
    private final WorkoutCategoryRepository workoutCategoryRepository;

    @Override
    public List<RoutineRecommendationResponseDTO> suggestRoutine(RoutineRecommendationRequestDTO requestDTO) {
        // 우선순위로 정렬
        List<WorkoutCategory> sortedCategories = workoutCategoryRepository.findByNameInOrderByPriority(requestDTO.getWorkoutCategoryList());

        // 부위가 3~5개일 때 추천 개수 설정
        Map<WorkoutCategory, Integer> workoutCountMap = allocateWorkoutCounts(sortedCategories);

        Set<String> selectedWorkouts = new HashSet<>();
        List<RoutineRecommendationResponseDTO> responseList = new ArrayList<>();
        int routineN = 0;

//        for (WorkoutCategory category : sortedCategories) {
//            int count = workoutCountMap.get(category);
//
//            List<Workout> workouts = workoutRepository.findNamesByCategory(category);
//            Collections.shuffle(workouts); // 랜덤 섞기
//
//            int added = 0;
//            for (Workout workout : workouts) {
//                if (added == 0) {
//                    responseList.add(new RoutineRecommendationResponseDTO(workout, new LinkedList<>()));
//                    added++;
//                }
//
//
//                if (selectedWorkouts.contains(workout)) continue;
//
//                selectedWorkouts.add(workout);
//                List<String> alternatives = workoutRepository.findSimilarWorkouts(workout, category.getId());
//                Collections.shuffle(alternatives);
//                List<String> top4 = alternatives.stream()
//                        .filter(alt -> !alt.equals(workout) && !selectedWorkouts.contains(alt))
//                        .limit(4)
//                        .toList();
//
//                selectedWorkouts.addAll(top4);
//
//                responseList.add(RoutineRecommendationResponse.builder()
//                        .mainWorkout(workout)
//                        .alternatives(top4)
//                        .build());
//
//                if (++added == count) break;
//            }
//        }


        return responseList;
    }

    private Map<WorkoutCategory, Integer> allocateWorkoutCounts(List<WorkoutCategory> sortedCategories) {
        Map<WorkoutCategory, Integer> workoutCountMap = new HashMap<>();
        WorkoutCategory workoutCategory;

        switch (sortedCategories.size()) {
            case 3:
                for (int i = 0; i < 2; i++) {
                    workoutCategory = sortedCategories.get(i);
                    workoutCountMap.put(workoutCategory, 2);
                }

                workoutCategory = sortedCategories.get(2);
                workoutCountMap.put(workoutCategory, 1);
                break;
            case 4:
                workoutCategory = sortedCategories.get(0);
                workoutCountMap.put(workoutCategory, 2);

                for (int i = 1; i < sortedCategories.size(); i++) {
                    workoutCategory = sortedCategories.get(i);
                    workoutCountMap.put(workoutCategory, 1);
                }
                break;
            case 5:
                for (int i = 0; i < sortedCategories.size(); i++) {
                    workoutCategory = sortedCategories.get(i);
                    workoutCountMap.put(workoutCategory, 1);
                }
                break;
        }

        return workoutCountMap;
    }
}
