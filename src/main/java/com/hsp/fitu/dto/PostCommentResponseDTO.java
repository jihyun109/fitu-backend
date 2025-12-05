package com.hsp.fitu.dto;

import java.time.LocalDateTime;
import java.util.List;

public record PostCommentResponseDTO(
        Long id,
        Long writerId,
        String writerName,
        String writerProfileImgUrl,
        Long rootId,
        String contents,
        LocalDateTime createdAt,
        Boolean isMine,
        Boolean isSecret,
        List<PostCommentResponseDTO> replies
){}
