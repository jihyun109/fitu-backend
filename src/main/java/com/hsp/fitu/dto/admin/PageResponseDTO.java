package com.hsp.fitu.dto.admin;

import java.util.List;

public record PageResponseDTO<T>(
        List<T> contents,
        int page,
        int size,
        long totalElements,
        int totalPage,
        boolean last
) {}
