package com.hsp.fitu.dto;

public record PostCommentCreateRequestDTO(
        Long postId,
        String contents,
        Long rootId
){}
