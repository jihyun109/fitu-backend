package com.hsp.fitu.repository;

import com.hsp.fitu.entity.BodyImageEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BodyImageRepository extends JpaRepository<BodyImageEntity, Long> {

    BodyImageEntity findFirstUrlByUserIdOrderByRecordedAtDesc(long userId);
}
