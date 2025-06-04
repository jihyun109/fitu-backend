package com.hsp.fitu.controller;

import com.hsp.fitu.dto.PhysicalInfoResponseDTO;
import com.hsp.fitu.dto.PhysicalInfoUpdateRequestDTO;
import com.hsp.fitu.dto.PhysicalInfoWeightHeightResponseDTO;
import com.hsp.fitu.dto.PhysicalInfosRequestDTO;
import com.hsp.fitu.jwt.CustomUserDetails;
import com.hsp.fitu.service.PhysicalInfoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/physical-infos")
@RequiredArgsConstructor
public class PhysicalInfoController {
    private final PhysicalInfoService physicalInfoService;

    @GetMapping()
    public ResponseEntity<PhysicalInfoResponseDTO> getPhysicsInfos(@AuthenticationPrincipal CustomUserDetails userDetails) {
        Long userId = userDetails.getId();
        return ResponseEntity.ok(physicalInfoService.getPhysicalInfo(userId));
    }

    @GetMapping("/muscle-bodyfat")
    public ResponseEntity<List<PhysicalInfoWeightHeightResponseDTO>> getWeightsAndHeights(@AuthenticationPrincipal CustomUserDetails userDetails, @RequestParam LocalDate startDate, @RequestParam LocalDate endDate) {
        Long userId = userDetails.getId();
        return ResponseEntity.ok(physicalInfoService.getMuscleAndBodyFat(userId, PhysicalInfosRequestDTO.builder()
                .startDate(startDate)
                .endDate(endDate)
                .build()));
    }

    @PostMapping()
    public ResponseEntity<String> updatePhysicalInfo(@AuthenticationPrincipal CustomUserDetails userDetails, @RequestBody PhysicalInfoUpdateRequestDTO physicalInfoUpdateRequestDTO) {
        Long userId = userDetails.getId();
        physicalInfoService.updatePhysicalInfo(userId, physicalInfoUpdateRequestDTO);
        return ResponseEntity.ok("physical info 업데이트 완료");
    }
}
