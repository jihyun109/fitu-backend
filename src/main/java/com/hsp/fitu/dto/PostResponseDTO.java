package com.hsp.fitu.dto;

import com.hsp.fitu.entity.enums.PostCategory;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class PostResponseDTO {
    private Long id;
    private PostCategory category;
    private Long universityId;
    private Long writerId;
    private String title;
    private String content;
    private LocalDateTime createdAt;
}
