package com.hsp.fitu.dto.admin;

import java.util.List;

public record PageResponseDTO<T>(
        List<T> contents,
        String totalPage
) {}
