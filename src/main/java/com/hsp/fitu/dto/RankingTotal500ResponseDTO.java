package com.hsp.fitu.dto;

import com.hsp.fitu.entity.Total500Info;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RankingTotal500ResponseDTO {
    private List<RankingItem> rankingItems;
    private RankingItem myRanking;
    private Total500Info myTotal500Record;
}

