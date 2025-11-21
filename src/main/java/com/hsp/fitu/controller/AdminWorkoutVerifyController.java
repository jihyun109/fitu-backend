package com.hsp.fitu.controller;

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

    @Operation(summary = "요청 문의/운동인증 리스트 by 조민기")
    @GetMapping("/")
    public ResponseEntity<PageResponseDTO<AdminWorkoutVerifyResponseDTO>> getWorkoutVerifyList () {

    }

    @Operation(summary = "요청 문의/운동인증 단일 by 조민기")
    @GetMapping("/")
    public ResponseEntity<PageResponseDTO<AdminWorkoutVerifyResponseDTO>> getWorkoutVerify() {

    }

    @Operation(summary = "요청 문의/운동인증 수락 by 조민기")
    @PostMapping("/")
    public ResponseEntity<PageResponseDTO<>> acceptWorkoutVerify (){

    }


    @Operation(summary = "요청 문의/운동인증 리스트 삭제 by 조민기")
    @DeleteMapping("/")
    public ResponseEntity<PageResponseDTO<>> denyWorkoutVerify (
            @PathVariable long id) {
        AdminWorkoutVerifyService.deleteVerify(id);
        return ResponseEntity.noContent().build();

    }
}
