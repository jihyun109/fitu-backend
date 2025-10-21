package com.hsp.fitu.service;

import com.hsp.fitu.dto.RankingItem;
import com.hsp.fitu.dto.RankingTotal500ResponseDTO;
import com.hsp.fitu.dto.RankingWorkoutCountResponseDTO;
import com.hsp.fitu.entity.Total500Info;
import com.hsp.fitu.repository.SessionRespository;
import com.hsp.fitu.repository.WorkoutVerificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class RankingServiceImpl implements RankingService {

    private final WorkoutVerificationRepository workoutVerificationRepository;
    private final SessionRespository sessionRespository;

    @Override
    public RankingTotal500ResponseDTO getTotal500Ranking(Long userId) {
        // 랭킹 리스트 get
        List<RankingItem> rankingItems = workoutVerificationRepository.findTotalRankingByUserId(userId);

        // 나의 랭킹 get
        RankingItem myRanking = workoutVerificationRepository.findRankingByUserId(userId);

        // 나의 3대 무게 get
        Total500Info myTotal500Info = workoutVerificationRepository.findTotal500InfoByUserId(userId);

        return RankingTotal500ResponseDTO.builder()
                .rankingItems(rankingItems)
                .myRanking(myRanking)
                .myTotal500Record(myTotal500Info)
                .build();
    }

    @Override
    public RankingWorkoutCountResponseDTO getWorkoutCountRanking(Long userId) {
        // 랭킹 list get
        List<RankingItem> rankingItems = sessionRespository.findAllRankingWorkoutCountByUserId(userId);

        return RankingWorkoutCountResponseDTO.builder()
                .rankingItems(rankingItems)
                .myRanking(null)
                .build();
    }
}
