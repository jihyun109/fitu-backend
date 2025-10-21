package com.hsp.fitu.controller;

import com.hsp.fitu.dto.RankingTotal500ResponseDTO;
import com.hsp.fitu.dto.RankingWorkoutCountResponseDTO;
import com.hsp.fitu.jwt.CustomUserDetails;
import com.hsp.fitu.service.RankingService;
import io.swagger.v3.oas.annotations.Operation;
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

    @Operation(summary = "3대 500 랭킹 조회 by 장지현")
    @GetMapping("/total500")
    public ResponseEntity<RankingTotal500ResponseDTO> getTotal500Ranking(@AuthenticationPrincipal CustomUserDetails userDetails) {

        return ResponseEntity.ok(rankingService.getTotal500Ranking(userDetails.getId()));
    }

    @Operation(summary = "규칙적 운동 랭킹 조회 by 장지현")
    @GetMapping("/workout-count")
    public ResponseEntity<RankingWorkoutCountResponseDTO> getWorkoutCountRanking(@AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(null);
    }
}
