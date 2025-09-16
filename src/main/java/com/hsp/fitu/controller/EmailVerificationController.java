package com.hsp.fitu.controller;

import com.hsp.fitu.dto.SendEmailRequestDTO;
import com.hsp.fitu.dto.VerifyEmailRequestDTO;
import com.hsp.fitu.service.EmailVerificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("auth/email")
public class EmailVerificationController {
    private final EmailVerificationService emailVerificationService;

    @PostMapping("/send")
    public ResponseEntity<Void> send(@RequestBody SendEmailRequestDTO req) {
        // 필요시 로그인 사용자와 req.email 매칭 검증
        emailVerificationService.sendVerificationCode(req.getEmail());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/verify")
    public ResponseEntity<Map<String, Object>> verify(@RequestBody VerifyEmailRequestDTO req) {
        boolean ok = emailVerificationService.verifyCode(req.getEmail(), req.getCode());
        if (ok) {
            // 여기서 사용자 Entity의 emailVerified=true 같은 상태 갱신 로직 호출
            return ResponseEntity.ok(Map.of("verified", true));
        }
        return ResponseEntity.status(400).body(Map.of("verified", false, "reason", "invalid_or_expired"));
    }

}
