package com.hsp.fitu.repository;

import com.hsp.fitu.entity.BodyImageEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BodyImageRepository extends JpaRepository<BodyImageEntity, Long> {

    @Query("SELECT b.url FROM BodyImageEntity b WHERE b.userId = :userId ORDER BY b.recordedAt DESC")
    String findMainImageUrlByUserIdAndOOrderByRecordedAtDesc(long userId);

    void deleteByUrl(String imageUrl);

    List<BodyImageEntity> findByUserIdOrderByRecordedAtDesc(long userId);
}
