package com.hsp.fitu.dto;

import java.util.List;

public record PostSliceResponseDTO<T>(
        List<T> content,
        boolean hasNext
) {}
