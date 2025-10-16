package com.hsp.fitu.dto;

import java.time.LocalDateTime;

public record PostResponseDTO(
      long id,
      String title,
      String writerName,
      String writerProfileImgUrl,
      String contents,
      LocalDateTime createdAt
) {}