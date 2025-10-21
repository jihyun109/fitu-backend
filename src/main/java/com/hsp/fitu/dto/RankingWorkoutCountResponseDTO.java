package com.hsp.fitu.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class RankingWorkoutCountResponseDTO {
    private List<RankingItem> rankingItems;
    private RankingItem myRanking;
}
