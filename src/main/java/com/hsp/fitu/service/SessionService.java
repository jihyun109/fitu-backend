package com.hsp.fitu.service;

import com.hsp.fitu.dto.SessionEndRequestDTO;
import com.hsp.fitu.dto.SessionEndResponseDTO;
import org.springframework.web.multipart.MultipartFile;

public interface SessionService {

    SessionEndResponseDTO saveSessionData(Long userId, SessionEndRequestDTO requestDTO, MultipartFile image);
}
