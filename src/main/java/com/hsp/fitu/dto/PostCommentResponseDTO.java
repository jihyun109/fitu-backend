package com.hsp.fitu.dto;

import java.time.LocalDateTime;

public record PostCommentResponseDTO(
        Long id,
        Long postId,
        Long writerId,
        Long rootId,
        String contents,
        LocalDateTime createdAt
){}
