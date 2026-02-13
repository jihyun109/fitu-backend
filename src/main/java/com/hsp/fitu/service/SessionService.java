package com.hsp.fitu.service;

import com.hsp.fitu.dto.SessionEndRequestDTO;
import com.hsp.fitu.dto.SessionEndResponseDTO;

public interface SessionService {

    SessionEndResponseDTO saveSessionData(Long userId, SessionEndRequestDTO requestDTO, String imageUrl);
}