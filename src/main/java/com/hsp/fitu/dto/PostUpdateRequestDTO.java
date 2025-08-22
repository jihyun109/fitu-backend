package com.hsp.fitu.dto;

import com.hsp.fitu.entity.enums.PostCategory;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostUpdateRequestDTO {
    private PostCategory category;
    private Long universityId;
    private Long writerId;
    private String title;
    private String content;
}
