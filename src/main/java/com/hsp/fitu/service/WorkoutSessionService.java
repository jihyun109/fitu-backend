package com.hsp.fitu.service;

import com.hsp.fitu.dto.SessionEndRequestDTO;
import com.hsp.fitu.dto.SessionEndResponseDTO;
import com.hsp.fitu.dto.SessionStartResponseDTO;
import org.springframework.web.multipart.MultipartFile;

public interface WorkoutSessionService {
    SessionStartResponseDTO startSession(Long userId);

    SessionEndResponseDTO endSession(Long userId, SessionEndRequestDTO requestDTO, MultipartFile image);
}
