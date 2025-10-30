package com.hsp.fitu.controller;

import com.hsp.fitu.dto.AdminSuspendRequestDTO;
import com.hsp.fitu.dto.AdminUserResponseDTO;
import com.hsp.fitu.service.AdminUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v2/admin")
public class AdminUserController {
    private final AdminUserService adminUserService;

    //전체 회원 조회
    @GetMapping
    public List<AdminUserResponseDTO> searchUsersByName(
            @RequestParam String name) {
        return adminUserService.searchUsersByName(name);
    }

    //계정 정지
    @PostMapping("/{userId}/suspend")
    public ResponseEntity<Void> suspendUser(
            @PathVariable Long userId,
            @RequestBody AdminSuspendRequestDTO dto) {
        adminUserService.suspendUser(userId, dto);
        return ResponseEntity.ok().build();
    }

    //정지 해제
    @PostMapping("/{userId}/unsuspend")
    public ResponseEntity<Void> unsuspendUser(
            @PathVariable Long userId) {
        adminUserService.unsuspendUser(userId);
        return ResponseEntity.ok().build();
    }
}
