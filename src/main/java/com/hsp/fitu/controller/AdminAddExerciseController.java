package com.hsp.fitu.controller;

import com.hsp.fitu.entity.ExerciseEquipmentsEntity;
import com.hsp.fitu.service.AdminAddExerciseService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v2/admin/equipments")
public class AdminAddExerciseController {
    private final AdminAddExerciseService adminAddExerciseService;

    @Operation(summary = "요청문의/운동기구 추가 by 조민기")
    @GetMapping("/{id}")
    public ResponseEntity<ExerciseEquipmentsEntity> getEquipment(
            @PathVariable Long id
    ){
        return ResponseEntity.ok(adminAddExerciseService.getEquipmentById(id));
    }

    @Operation(summary = "요청문의/운동기구 추가 전체 리스트 by 조민기")
    @GetMapping
    public ResponseEntity<List<ExerciseEquipmentsEntity>> getAllEquipments() {
        return ResponseEntity.ok(adminAddExerciseService.getAllEquipments());
    }

}
