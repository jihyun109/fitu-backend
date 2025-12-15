package com.hsp.fitu.service;

import com.hsp.fitu.dto.*;
import com.hsp.fitu.entity.OldWorkoutCategoryEntity;
import com.hsp.fitu.entity.OldWorkoutEntity;
import com.hsp.fitu.entity.WorkoutEntity;
import com.hsp.fitu.entity.enums.Workout;
import com.hsp.fitu.entity.enums.WorkoutCategory;
import com.hsp.fitu.error.ErrorCode;
import com.hsp.fitu.error.customExceptions.EmptyFileException;
import com.hsp.fitu.error.customExceptions.WorkoutNotFoundException;
import com.hsp.fitu.repository.WorkoutCategoryRepository;
import com.hsp.fitu.repository.OldWorkoutRepository;
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
    private final OldWorkoutRepository oldWorkoutRepository;
    private final WorkoutRepository workoutRepository;
    private final WorkoutCategoryRepository workoutCategoryRepository;
    private final S3Service s3Service;

    @Override
    public List<OldRoutineResponseDTO> suggestRoutine(RoutineRecommendationRequestDTO requestDTO) {
        List<OldWorkoutCategoryEntity> sortedCategories =
                workoutCategoryRepository.findByNameInOrderByPriority(requestDTO.getWorkoutCategoryList());

        Map<WorkoutCategory, Integer> workoutCountMap = allocateWorkoutCounts(sortedCategories);

        Set<Workout> selectedMainWorkouts = new HashSet<>();
        List<OldRoutineResponseDTO> responseList = new ArrayList<>();

        for (OldWorkoutCategoryEntity category : sortedCategories) {

            WorkoutCategory categoryName = category.getName();
            int count = workoutCountMap.get(categoryName);

            List<OldWorkoutEntity> allWorkouts = oldWorkoutRepository.findAllByCategoryId(category.getId());
            Collections.shuffle(allWorkouts);

            int added = 0;

            // mainWorkout 선정
            for (OldWorkoutEntity workout : allWorkouts) {
                Workout mainWorkout = workout.getName();
                if (selectedMainWorkouts.contains(mainWorkout)) continue;

                // similar workout 선정
                List<OldWorkoutEntity> similarCandidates = oldWorkoutRepository.findSimilarWorkouts(mainWorkout, category.getId());
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

                responseList.add(OldRoutineResponseDTO.builder()
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
    public WorkoutSelectResponseDTO recommendRoutine(RoutineRecommendationRequestDTO requestDTO) {
        // 1. 요청된 카테고리 이름 목록을 우선순위(priority) 순서대로 DB에서 조회
        List<OldWorkoutCategoryEntity> sortedCategories =
                workoutCategoryRepository.findByNameInOrderByPriority(requestDTO.getWorkoutCategoryList());

        // 2. 각 카테고리별 추천할 운동 개수를 계산 (할당)
        Map<WorkoutCategory, Integer> workoutCountMap = allocateWorkoutCounts(sortedCategories);

        // 3. 이미 선택된 main workout을 저장할 Set (중복 방지)
        Set<Workout> selectedMainWorkouts = new HashSet<>();

        // 4. 최종 추천 루틴을 담을 리스트
        List<SelectedWorkout> responseList = new ArrayList<>();


        System.out.println("workoutCountMap: ");
        for (Map.Entry<WorkoutCategory, Integer> entry : workoutCountMap.entrySet()) {
            System.out.println(entry.getKey() + " " + entry.getValue());
        }

        // 5. 카테고리 순회하며 추천 운동 생성
        for (OldWorkoutCategoryEntity category : sortedCategories) {

            // 카테고리 이름과 할당된 운동 개수 가져오기
            WorkoutCategory categoryName = category.getName();

            System.out.println();

            System.out.println(categoryName);

            int count = workoutCountMap.get(categoryName);

            // 해당 카테고리의 모든 운동 조회 후 랜덤 섞기
            List<WorkoutEntity> allWorkouts = workoutRepository.findAllByCategoryId(category.getId());
            Collections.shuffle(allWorkouts);

            int added = 0; // 현재 카테고리에서 선택된 운동 수

            // 6. mainWorkout 선정
            for (WorkoutEntity workout : allWorkouts) {
                Workout mainWorkout = workout.getName();

                // 이미 선택된 운동이면 건너뜀
                if (selectedMainWorkouts.contains(mainWorkout)) continue;

                // mainWorkout을 선택된 운동 Set에 추가
                selectedMainWorkouts.add(mainWorkout);

                // 9. mainWorkout의 이미지 URL 가져오기
                String gifUrl = workout.getGifUrl();

                responseList.add(new SelectedWorkout(workout.getId(), workout.getWorkoutName(), workout.getWorkoutDescription(), workout.getImageUrl(), gifUrl));

                // 12. 카테고리별로 할당된 운동 개수(count)만큼 선택하면 break
                if (++added == count) break;
            }
        }

        return WorkoutSelectResponseDTO.builder()
                .selectedWorkouts(responseList)
                .build();
    }

    @Override
    public WorkoutSelectResponseDTO selectRoutine(WorkoutCustomRequestDTO requestDTO) {
        List<SelectedWorkout> selectedWorkouts = workoutRepository.findAllByIds(requestDTO.getWorkoutIds());
        return WorkoutSelectResponseDTO.builder()
                .selectedWorkouts(selectedWorkouts)
                .build();
    }

    @Override
    public List<WorkoutGifResponseDTO> getWorkoutGifs(WorkoutGifRequestDTO requestDTO) {
        List<Workout> workouts = requestDTO.getWorkouts();

        return workouts.stream()
                .map(workout -> {
                    OldWorkoutEntity entity = oldWorkoutRepository.findByName(workout);

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
        OldWorkoutEntity workout = oldWorkoutRepository.findById(workoutId)
                .orElseThrow(() -> new WorkoutNotFoundException(ErrorCode.INVALID_WORKOUT_ID));

        if (image.isEmpty()) {
            throw new EmptyFileException(ErrorCode.EMPTY_FILE);
        }

        String newImageUrl = s3Service.uploadFileToS3(image, "workouts/images/");
        workout.updateImageUrl(newImageUrl);
    }

    @Transactional
    @Override
    public void updateWorkoutGif(long workoutId, MultipartFile gif) {
        OldWorkoutEntity workout = oldWorkoutRepository.findById(workoutId)
                .orElseThrow(() -> new WorkoutNotFoundException(ErrorCode.INVALID_WORKOUT_ID));

        if (gif.isEmpty()) {
            throw new EmptyFileException(ErrorCode.EMPTY_FILE);
        }

        String newGifUrl = s3Service.uploadFileToS3(gif, "workouts/gifs/");
        workout.updateGifUrl(newGifUrl);
    }


    private Map<WorkoutCategory, Integer> allocateWorkoutCounts(List<OldWorkoutCategoryEntity> sortedCategories) {
        Map<WorkoutCategory, Integer> workoutCountMap = new HashMap<>();
        WorkoutCategory workoutCategory;

        switch (sortedCategories.size()) {
            case 1:
                // 카테고리가 1개일 경우 5개 배정
                workoutCategory = sortedCategories.get(0).getName();
                workoutCountMap.put(workoutCategory, 5);
                break;
            case 2:
                // 카테고리가 2개일 경우 3개, 2개 배정
                workoutCategory = sortedCategories.get(0).getName();
                workoutCountMap.put(workoutCategory, 3);

                workoutCategory = sortedCategories.get(1).getName();
                workoutCountMap.put(workoutCategory, 2);
                break;
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
