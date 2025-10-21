package com.hsp.fitu.service;

import com.hsp.fitu.dto.RankingTotal500ResponseDTO;

public interface RankingService {
    RankingTotal500ResponseDTO getTotal500Ranking(Long userId);
}
