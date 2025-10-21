package com.hsp.fitu.controller;

import com.hsp.fitu.dto.RankingTotal500ResponseDTO;
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
@RequestMapping("/api/v2/ranking")
public class RankingController {

    private final RankingService rankingService;

    @GetMapping("/total500")
    public ResponseEntity<RankingTotal500ResponseDTO> getTotal500Ranking(@AuthenticationPrincipal CustomUserDetails userDetails) {

        return ResponseEntity.ok(rankingService.getTotal500Ranking(userDetails.getId()));
    }
}
