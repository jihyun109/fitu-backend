package com.hsp.fitu.dto;

import lombok.Data;

@Data
public class PostCommentCreateRequestDTO {
    private Long postId;
    private String contents;
    private Long targetCommentId;
}
