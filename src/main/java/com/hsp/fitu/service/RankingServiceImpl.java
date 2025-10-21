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
        List<RankingItem> rankingItems = workoutVerificationRepository.getTotal500Ranking(userId);

        return RankingTotal500ResponseDTO.builder()
                .rankingItems(rankingItems)
                .build();
    }
}
