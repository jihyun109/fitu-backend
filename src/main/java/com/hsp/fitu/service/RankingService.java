package com.hsp.fitu.service;

import com.hsp.fitu.dto.RankingTotal500ResponseDTO;
import com.hsp.fitu.dto.RankingWorkoutCountResponseDTO;

public interface RankingService {
    RankingTotal500ResponseDTO getTotal500Ranking(Long userId);

    RankingWorkoutCountResponseDTO getWorkoutCountRanking(Long userId);
}
