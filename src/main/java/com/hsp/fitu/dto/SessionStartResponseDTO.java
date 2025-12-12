package com.hsp.fitu.dto;

import java.time.LocalDateTime;

public record SessionStartResponseDTO(
        Long sessionId,
        LocalDateTime startTime
) {
}
