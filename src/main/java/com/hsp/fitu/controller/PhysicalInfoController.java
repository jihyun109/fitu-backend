package com.hsp.fitu.controller;

import com.hsp.fitu.dto.PhysicalInfoResponseDTO;
import com.hsp.fitu.dto.PhysicalInfoUpdateRequestDTO;
import com.hsp.fitu.jwt.CustomUserDetails;
import com.hsp.fitu.service.PhysicalInfoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v2/physical-infos")
@RequiredArgsConstructor
public class PhysicalInfoController {
    private final PhysicalInfoService physicalInfoService;

    // 사용자 가장 최근 신체 정보 & 사용자 이름 조회
    @GetMapping()
    public ResponseEntity<PhysicalInfoResponseDTO> getPhysicsInfos(@AuthenticationPrincipal CustomUserDetails userDetails) {
        Long userId = userDetails.getId();
        return ResponseEntity.ok(physicalInfoService.getPhysicalInfo(userId));
    }

    @PostMapping()
    public ResponseEntity<String> updatePhysicalInfo(@AuthenticationPrincipal CustomUserDetails userDetails, @RequestBody PhysicalInfoUpdateRequestDTO physicalInfoUpdateRequestDTO) {
        Long userId = userDetails.getId();
        physicalInfoService.updatePhysicalInfo(userId, physicalInfoUpdateRequestDTO);
        return ResponseEntity.ok("Physical information successfully updated.");
    }
}
