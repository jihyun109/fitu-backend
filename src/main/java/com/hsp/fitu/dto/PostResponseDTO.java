package com.hsp.fitu.dto;

import com.hsp.fitu.entity.enums.PostCategory;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class PostResponseDTO {
    private Long id;
    private PostCategory category;
    private Long universityId;
    private Long writerId;
    private String title;
    private String contents;
    private LocalDateTime createdAt;
}
