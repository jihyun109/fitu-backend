package com.hsp.fitu.controller;

import com.hsp.fitu.dto.admin.AdminWorkoutVerifyDetailResponseDTO;
import com.hsp.fitu.dto.admin.AdminWorkoutVerifyResponseDTO;
import com.hsp.fitu.dto.admin.PageResponseDTO;
import com.hsp.fitu.service.AdminWorkoutVerifyService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v2/admin")
public class AdminWorkoutVerifyController {

    private final AdminWorkoutVerifyService adminWorkoutVerifyService;

    @Operation(summary = "요청 문의/운동인증 리스트 by 조민기")
    @GetMapping("/")
    public ResponseEntity<PageResponseDTO<AdminWorkoutVerifyResponseDTO>> getWorkoutVerifyList (
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size
    ) {
        return ResponseEntity.ok(adminWorkoutVerifyService.getPendingVerify(page, size));
    }

    @Operation(summary = "요청 문의/운동인증 상세 by 조민기")
    @GetMapping("/{id}")
    public ResponseEntity<AdminWorkoutVerifyDetailResponseDTO> getWorkoutVerify(
            @PathVariable long id
    ) {
        return ResponseEntity.ok(adminWorkoutVerifyService.getVerifyDetail(id));
    }

    @Operation(summary = "요청 문의/운동인증 수락 by 조민기")
    @PostMapping("/{id}")
    public ResponseEntity<Void> approveVerification (
            @PathVariable long id,
            @RequestParam int weight
    ){
        adminWorkoutVerifyService.acceptedVerify(id, weight);
        return ResponseEntity.ok().build();
    }


    @Operation(summary = "요청 문의/운동인증 리스트 삭제 by 조민기")
    @DeleteMapping("/{verifyId}")
    public ResponseEntity<Void> deleteVerifyId(
            @PathVariable long verifyId) {
        adminWorkoutVerifyService.deleteVerifyId(verifyId);
        return ResponseEntity.noContent().build();

    }
}
