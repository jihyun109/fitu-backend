package com.hsp.fitu.dto;

import com.hsp.fitu.entity.enums.PostCategory;
import lombok.Data;

@Data
public class PostCreateRequestDTO {
    private Long id;
    private PostCategory category;
    private Long universityId;
    private Long writerId;
    private String title;
    private String contents;
}
