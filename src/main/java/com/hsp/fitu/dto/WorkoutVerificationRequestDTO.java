package com.hsp.fitu.dto;

import com.hsp.fitu.entity.enums.WorkoutVerificationType;
import lombok.Builder;
import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

@Builder
@Getter
public class WorkoutVerificationRequestDTO {
    private Long userId;
    private WorkoutVerificationType workoutVerificationType;
    private MultipartFile workoutVerificationVideo;
}


