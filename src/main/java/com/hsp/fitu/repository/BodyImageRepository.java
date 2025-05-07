package com.hsp.fitu.repository;

import com.hsp.fitu.entity.BodyImageEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface BodyImageRepository extends JpaRepository<BodyImageEntity, Long> {

    BodyImageEntity findFirstUrlByUserIdOrderByRecordedAtDesc(long userId);

    @Modifying
    @Query("DELETE FROM BodyImageEntity b WHERE b.url = :imageUrl")
    @Transactional
    void deleteByUrl(String imageUrl);

    List<BodyImageEntity> findByUserIdOrderByRecordedAtDesc(long userId);
}
