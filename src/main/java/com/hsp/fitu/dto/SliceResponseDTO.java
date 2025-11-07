package com.hsp.fitu.dto;

import java.util.List;

public record SliceResponseDTO<T>(
        List<T> contents,
        boolean hasNext
) {}