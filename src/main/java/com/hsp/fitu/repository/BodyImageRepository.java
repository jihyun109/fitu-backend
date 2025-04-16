package com.hsp.fitu.repository;

import com.hsp.fitu.entity.BodyPhotoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface BodyImageRepository extends JpaRepository<BodyPhotoEntity, Long> {

    @Query("SELECT b.imageUrl FROM BodyPhotoEntity b WHERE b.userId = :userId ORDER BY b.recordedAt DESC")
    String findMainImageUrlByUserIdAndOOrderByRecordedAtDesc(long userId);
}
