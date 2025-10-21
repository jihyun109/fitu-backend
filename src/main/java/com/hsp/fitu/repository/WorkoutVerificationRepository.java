package com.hsp.fitu.repository;

import com.hsp.fitu.dto.RankingTotal500ResponseDTO;
import com.hsp.fitu.entity.WorkoutVerificationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WorkoutVerificationRepository extends JpaRepository<WorkoutVerificationEntity, Long> {
    RankingTotal500ResponseDTO getTotal500Ranking(Long userId);
}
