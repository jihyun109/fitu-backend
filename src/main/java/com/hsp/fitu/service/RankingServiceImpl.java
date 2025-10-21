package com.hsp.fitu.service;

import com.hsp.fitu.dto.RankingItem;
import com.hsp.fitu.dto.RankingTotal500ResponseDTO;
import com.hsp.fitu.repository.WorkoutVerificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class RankingServiceImpl implements RankingService {

    private final WorkoutVerificationRepository workoutVerificationRepository;

    @Override
    public RankingTotal500ResponseDTO getTotal500Ranking(Long userId) {
        // 랭킹 리스트 get
        List<RankingItem> rankingItems = workoutVerificationRepository.getTotal500Ranking(userId);

        // 나의 랭킹 get
        RankingItem myRanking = workoutVerificationRepository.getMyRanking(userId);

        return RankingTotal500ResponseDTO.builder()
                .rankingItems(rankingItems)
                .myRanking(myRanking)
                .build();
    }
}
