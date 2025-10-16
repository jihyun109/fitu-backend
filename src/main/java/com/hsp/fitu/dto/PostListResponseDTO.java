package com.hsp.fitu.dto;

import com.hsp.fitu.entity.enums.PostCategory;

import java.time.LocalDateTime;

public record PostListResponseDTO(
        long id,
        String universityName,
        PostCategory category,
        String title,
        String contents,
        LocalDateTime createdAt
) {
}
