package com.hsp.fitu.service;

import com.hsp.fitu.dto.*;
import com.hsp.fitu.entity.OldWorkoutCategoryEntity;
import com.hsp.fitu.entity.OldWorkoutEntity;
import com.hsp.fitu.entity.enums.Workout;
import com.hsp.fitu.entity.enums.WorkoutCategory;
import com.hsp.fitu.error.ErrorCode;
import com.hsp.fitu.error.customExceptions.EmptyFileException;
import com.hsp.fitu.error.customExceptions.WorkoutNotFoundException;
import com.hsp.fitu.repository.WorkoutCategoryRepository;
import com.hsp.fitu.repository.WorkoutRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class WorkoutServiceImpl implements WorkoutService {
    private final WorkoutRepository workoutRepository;
    private final WorkoutCategoryRepository workoutCategoryRepository;
    private final S3ImageService s3ImageService;

    @Override
    public List<RoutineRecommendationResponseDTO> suggestRoutine(RoutineRecommendationRequestDTO requestDTO) {
        List<OldWorkoutCategoryEntity> sortedCategories =
                workoutCategoryRepository.findByNameInOrderByPriority(requestDTO.getWorkoutCategoryList());

        Map<WorkoutCategory, Integer> workoutCountMap = allocateWorkoutCounts(sortedCategories);

        Set<Workout> selectedMainWorkouts = new HashSet<>();
        List<RoutineRecommendationResponseDTO> responseList = new ArrayList<>();

        for (OldWorkoutCategoryEntity category : sortedCategories) {

            WorkoutCategory categoryName = category.getName();
            int count = workoutCountMap.get(categoryName);

            List<OldWorkoutEntity> allWorkouts = workoutRepository.findAllByCategoryId(category.getId());
            Collections.shuffle(allWorkouts);

            int added = 0;

            // mainWorkout 선정
            for (OldWorkoutEntity workout : allWorkouts) {
                Workout mainWorkout = workout.getName();
                if (selectedMainWorkouts.contains(mainWorkout)) continue;

                // similar workout 선정
                List<OldWorkoutEntity> similarCandidates = workoutRepository.findSimilarWorkouts(mainWorkout, category.getId());
                Collections.shuffle(similarCandidates);

                selectedMainWorkouts.add(mainWorkout);

                // body part 받아오기
                long bodyPartId = workout.getCategoryId();
                Optional<OldWorkoutCategoryEntity> workoutCategoryEntity = workoutCategoryRepository.findById(bodyPartId);
                WorkoutCategory bodyPart = workoutCategoryEntity.get().getName();

                // response 생성
                String mainImageUrl = workout.getImageUrl();

                List<WorkoutWithImageDTO> similarList = similarCandidates.stream()
                        .filter(alt -> !alt.equals(workout))
                        .limit(4)
                        .map(alt -> WorkoutWithImageDTO.builder()
                                .name(alt.getName())
                                .imageUrl(alt.getImageUrl()) // 이미지 URL 조회
                                .build())
                        .toList();

                responseList.add(RoutineRecommendationResponseDTO.builder()
                        .mainWorkout(WorkoutWithImageDTO.builder()
                                .name(mainWorkout)
                                .imageUrl(mainImageUrl).build())
                        .similarWorkouts(similarList)
                        .bodyPart(bodyPart)
                        .build());

                if (++added == count) break;
            }
        }

        return responseList;
    }

    @Override
    public List<WorkoutGifResponseDTO> getWorkoutGifs(WorkoutGifRequestDTO requestDTO) {
        List<Workout> workouts = requestDTO.getWorkouts();

        return workouts.stream()
                .map(workout -> {
                    OldWorkoutEntity entity = workoutRepository.findByName(workout);

                    if (entity == null) {
                        throw new WorkoutNotFoundException(ErrorCode.WORKOUT_NOT_FOUND);
                    }

                    return WorkoutGifResponseDTO.builder()
                            .workoutName(entity.getName())
                            .gif(entity.getGifUrl())
                            .build();
                })
                .toList();
    }

    @Transactional
    @Override
    public void updateWorkoutImage(long workoutId, MultipartFile image) {
        OldWorkoutEntity workout = workoutRepository.findById(workoutId)
                .orElseThrow(() -> new WorkoutNotFoundException(ErrorCode.INVALID_WORKOUT_ID));

        if (image.isEmpty()) {
            throw new EmptyFileException(ErrorCode.EMPTY_FILE);
        }

        String newImageUrl = s3ImageService.uploadFileToS3(image, "workouts/images/");
        workout.updateImageUrl(newImageUrl);
    }

    @Transactional
    @Override
    public void updateWorkoutGif(long workoutId, MultipartFile gif) {
        OldWorkoutEntity workout = workoutRepository.findById(workoutId)
                .orElseThrow(() -> new WorkoutNotFoundException(ErrorCode.INVALID_WORKOUT_ID));

        if (gif.isEmpty()) {
            throw new EmptyFileException(ErrorCode.EMPTY_FILE);
        }

        String newGifUrl = s3ImageService.uploadFileToS3(gif, "workouts/gifs/");
        workout.updateGifUrl(newGifUrl);
    }


    private Map<WorkoutCategory, Integer> allocateWorkoutCounts(List<OldWorkoutCategoryEntity> sortedCategories) {
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
