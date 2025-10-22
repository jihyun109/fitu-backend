package com.hsp.fitu.dto;

import java.util.List;

public record PostSliceResponseDTO<T>(
        String universityName,
        List<T> content,
        boolean hasNext
) {}
