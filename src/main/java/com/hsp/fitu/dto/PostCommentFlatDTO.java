package com.hsp.fitu.dto;

import java.time.LocalDateTime;

public record PostCommentFlatDTO(
        Long id,
        String writerName,
        String writerProfileImgUrl,
        Long rootId,
        String contents,
        LocalDateTime createdAt,
        Boolean isMine,
        Boolean isSecret
) {}
