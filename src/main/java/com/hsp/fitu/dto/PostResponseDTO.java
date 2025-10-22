package com.hsp.fitu.dto;

import com.hsp.fitu.entity.enums.PostCategory;

import java.time.LocalDateTime;

public record PostResponseDTO(
      long id,
      PostCategory postCategory,
      String title,
      String writerName,
      String writerProfileImgUrl,
      String contents,
      LocalDateTime createdAt
) {}