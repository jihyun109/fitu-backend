package com.hsp.fitu.service;

import com.hsp.fitu.dto.*;
import com.hsp.fitu.entity.*;
import com.hsp.fitu.error.BusinessException;
import com.hsp.fitu.error.ErrorCode;
import com.hsp.fitu.repository.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SessionServiceImplTest {

    @Mock private SessionRepository sessionRepository;
    @Mock private MediaFilesRepository mediaFilesRepository;
    @Mock private WorkoutNewRepository workoutNewRepository;
    @Mock private SessionExerciseRepository sessionExerciseRepository;
    @Mock private WorkoutBulkRepository workoutBulkRepository;

    @InjectMocks
    private SessionServiceImpl sessionService;

    // ---- 헬퍼 ----

    private SessionEndRequestDTO buildRequest(int totalMinutes) {
        WorkoutSetRequestDTO set = new WorkoutSetRequestDTO(1, 60, 10);
        SessionExerciseRequestDTO exercise = new SessionExerciseRequestDTO("벤치프레스", 1, List.of(set));
        return new SessionEndRequestDTO(totalMinutes, List.of(exercise));
    }

    /** 정상 저장 흐름에 필요한 repository 목 설정 */
    private void stubForNormalSave() {
        WorkoutEntity workout = mock(WorkoutEntity.class);
        when(workout.getWorkoutName()).thenReturn("벤치프레스");
        when(workout.getId()).thenReturn(10L);
        when(workoutNewRepository.findByWorkoutNameIn(anyList())).thenReturn(List.of(workout));

        when(sessionRepository.save(any())).thenReturn(SessionsEntity.builder().build());

        // saveAll은 받은 리스트를 그대로 반환해야 setsMap의 키(entity 참조)가 일치한다
        when(sessionExerciseRepository.saveAll(anyList()))
                .thenAnswer(inv -> inv.getArgument(0));
        // workoutBulkRepository.bulkInsertSets()는 void → Mockito 기본값(doNothing)
    }

    // ---- 테스트 ----

    @Test
    @DisplayName("totalMinutes가 0이면 INVALID_TOTAL_MIN 예외가 발생한다")
    void saveSessionData_zeroMinutes_throwsInvalidTotalMin() {
        assertThatThrownBy(() -> sessionService.saveSessionData(1L, buildRequest(0), null))
                .isInstanceOf(BusinessException.class)
                .extracting(e -> ((BusinessException) e).getErrorCode())
                .isEqualTo(ErrorCode.INVALID_TOTAL_MIN);
    }

    @Test
    @DisplayName("totalMinutes가 음수이면 INVALID_TOTAL_MIN 예외가 발생한다")
    void saveSessionData_negativeMinutes_throwsInvalidTotalMin() {
        assertThatThrownBy(() -> sessionService.saveSessionData(1L, buildRequest(-10), null))
                .isInstanceOf(BusinessException.class)
                .extracting(e -> ((BusinessException) e).getErrorCode())
                .isEqualTo(ErrorCode.INVALID_TOTAL_MIN);
    }

    @Test
    @DisplayName("정상 세션 저장 시 result가 OK인 응답이 반환된다")
    void saveSessionData_validRequest_returnsOk() {
        stubForNormalSave();

        SessionEndResponseDTO result = sessionService.saveSessionData(1L, buildRequest(30), null);

        assertThat(result.result()).isEqualTo("OK");
    }

    @Test
    @DisplayName("imageUrl이 null이면 미디어 파일이 저장되지 않는다")
    void saveSessionData_nullImageUrl_doesNotSaveMedia() {
        stubForNormalSave();

        sessionService.saveSessionData(1L, buildRequest(30), null);

        verify(mediaFilesRepository, never()).save(any());
    }

    @Test
    @DisplayName("imageUrl이 있으면 미디어 파일이 저장된다")
    void saveSessionData_withImageUrl_savesMedia() {
        stubForNormalSave();
        when(mediaFilesRepository.save(any())).thenReturn(MediaFilesEntity.builder().build());

        sessionService.saveSessionData(1L, buildRequest(30), "https://s3.example.com/img.jpg");

        verify(mediaFilesRepository).save(any());
    }

    @Test
    @DisplayName("totalMinutes=30이면 startTime은 현재 시각으로부터 약 30분 이전이다")
    void saveSessionData_startTimeCalculation_isApproximatelyNowMinusTotalMinutes() {
        stubForNormalSave();

        LocalDateTime before = LocalDateTime.now().minusMinutes(31);
        sessionService.saveSessionData(1L, buildRequest(30), null);
        LocalDateTime after = LocalDateTime.now().minusMinutes(29);

        ArgumentCaptor<SessionsEntity> captor = ArgumentCaptor.forClass(SessionsEntity.class);
        verify(sessionRepository).save(captor.capture());

        LocalDateTime startTime = captor.getValue().getStartTime();
        assertThat(startTime)
                .as("startTime은 now-31min 이후이고 now-29min 이전이어야 한다")
                .isAfter(before)
                .isBefore(after);
    }
}
