package com.hsp.fitu.repository;

import com.hsp.fitu.entity.MediaFilesEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface MediaFilesRepository extends JpaRepository<MediaFilesEntity, Long> {

    @Modifying
    @Query("""
            DELETE
            FROM MediaFilesEntity m
            WHERE m.url = :imageUrl
            """)
    void deleteByUrl(@Param("imageUrl") String imageUrl);
}
