package com.hsp.fitu.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class PostCommentResponseDTO {
    private Long id;
    private Long postId;
    private Long writerId;
    private Long rootId;
    private String contents;
    private LocalDateTime createdAt;
}
