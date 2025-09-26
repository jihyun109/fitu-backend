package com.hsp.fitu.dto;

import com.hsp.fitu.entity.enums.PostCategory;

public record PostCreateRequestDTO(
       PostCategory category,
       String title,
       String contents
){}
