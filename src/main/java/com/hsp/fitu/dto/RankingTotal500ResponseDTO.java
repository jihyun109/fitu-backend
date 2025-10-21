package com.hsp.fitu.dto;

import com.hsp.fitu.entity.Total500Info;
import lombok.Getter;

import java.util.List;

@Getter
public class RankingTotal500ResponseDTO {
    private List<RankingItem> rankingItems;
    private RankingItem myRanking;
    private Total500Info myTotal500Record;
}

