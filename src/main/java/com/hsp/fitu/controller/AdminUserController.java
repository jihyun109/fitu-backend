package com.hsp.fitu.controller;

import com.hsp.fitu.dto.AdminSuspendRequestDTO;
import com.hsp.fitu.dto.AdminUserResponseDTO;
import com.hsp.fitu.service.AdminUserService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v2/admin")
public class AdminUserController {
    private final AdminUserService adminUserService;

    @Operation(summary = "회원 관리 by 조민기")
    @GetMapping
    public List<AdminUserResponseDTO> searchUsersByName(
            @RequestParam String name) {
        return adminUserService.searchUsersByName(name);
    }

    @Operation(summary = "계정 정지 by 조민기")
    @PostMapping("/{userId}/suspend")
    public ResponseEntity<Void> suspendUser(
            @PathVariable Long userId,
            @RequestBody AdminSuspendRequestDTO dto) {
        adminUserService.suspendUser(userId, dto);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "정지 해제 by 조민기")
    @PostMapping("/{userId}/unsuspend")
    public ResponseEntity<Void> unsuspendUser(
            @PathVariable Long userId) {
        adminUserService.unsuspendUser(userId);
        return ResponseEntity.ok().build();
    }
}
