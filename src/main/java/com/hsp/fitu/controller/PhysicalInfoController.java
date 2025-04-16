package com.hsp.fitu.controller;

import com.hsp.fitu.dto.PhysicalInfoResponseDTO;
import com.hsp.fitu.dto.PhysicalInfoUpdateRequestDTO;
import com.hsp.fitu.dto.PhysicalInfoWeightHeightResponseDTO;
import com.hsp.fitu.dto.PhysicalInfosRequestDTO;
import com.hsp.fitu.service.PhysicalInfoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/physical-infos")
@RequiredArgsConstructor
public class PhysicalInfoController {
    private final PhysicalInfoService physicalInfoService;

    @GetMapping("/{userId}")
    public ResponseEntity<PhysicalInfoResponseDTO> getPhysicsInfos(@PathVariable long userId) {
        return ResponseEntity.ok(physicalInfoService.getPhysicalInfo(userId));
    }

    @GetMapping("/{userId}/muscle-bodyfat")
    public ResponseEntity<List<PhysicalInfoWeightHeightResponseDTO>> getWeightsAndHeights(@PathVariable long userId, @RequestBody PhysicalInfosRequestDTO physicalInfosRequestDTO) {
        return ResponseEntity.ok(physicalInfoService.getMuscleAndBodyFat(userId, physicalInfosRequestDTO));
    }

    @PostMapping("/{userId}")
    public ResponseEntity<String> updatePhysicalInfo(@PathVariable long userId, @RequestBody PhysicalInfoUpdateRequestDTO physicalInfoUpdateRequestDTO) {
        physicalInfoService.updatePhysicalInfo(userId, physicalInfoUpdateRequestDTO);
        return ResponseEntity.ok("physical info 업데이트 완료");
    }
}
