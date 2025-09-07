package com.hsp.fitu.dto;

import lombok.Data;

@Data
public class PostCommentCreateRequestDTO {
    private Long postId;
    private Long writerId;
    private Long rootId;
    private String contents;
}
