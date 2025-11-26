package com.hsp.fitu.dto;

public record EquipmentResponseDto(
        Long id,
        String equipmentName,
        String imageUrl,
        String gifUrl
) {}