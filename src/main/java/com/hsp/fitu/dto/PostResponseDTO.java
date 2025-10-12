package com.hsp.fitu.dto;

import com.hsp.fitu.entity.enums.PostCategory;

import java.time.LocalDateTime;

public record PostResponseDTO(
      long id,
      long universityId,
      PostCategory category,
      String title,
      String contents,
       LocalDateTime createdAt
) {}