package com.hsp.fitu.dto;

import lombok.Getter;

@Getter
public class RankingItem {
    private int rank;
    private String name;
    private int value;
    private RankingItem rankingItem;
    private String profileImageUrl;
}
