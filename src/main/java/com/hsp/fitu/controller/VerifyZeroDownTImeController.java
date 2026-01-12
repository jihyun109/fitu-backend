package com.hsp.fitu.controller;

import com.hsp.fitu.jwt.CustomUserDetails;
import com.hsp.fitu.service.RankingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/test")
public class VerifyZeroDownTImeController {
    private final RankingService rankingService;

    @GetMapping()
    public ResponseEntity<String> getTotal500Ranking(@AuthenticationPrincipal CustomUserDetails userDetails) {
        rankingService.getTotal500Ranking(userDetails.getId());

        return ResponseEntity.ok("Hello FitU V2");
    }

}
