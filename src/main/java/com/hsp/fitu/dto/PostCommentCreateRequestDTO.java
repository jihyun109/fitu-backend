package com.hsp.fitu.dto;

public record PostCommentCreateRequestDTO(
        String contents,
        Long rootId
){}
