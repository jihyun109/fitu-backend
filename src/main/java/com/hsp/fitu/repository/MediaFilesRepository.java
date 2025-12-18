package com.hsp.fitu.repository;

import com.hsp.fitu.dto.ProfileImage;
import com.hsp.fitu.entity.MediaFilesEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MediaFilesRepository extends JpaRepository<MediaFilesEntity, Long> {
    @Query("""
            SELECT new com.hsp.fitu.dto.ProfileImage(m.url)
            FROM MediaFilesEntity m
            JOIN UserEntity u ON m.id = u.profileImgId
            WHERE u.id = :userId
            ORDER BY m.uploadedAt
            """)
    List<ProfileImage> findProfileImgsByUserId(long userId);


    @Modifying
    @Query("""
            DELETE
            FROM MediaFilesEntity m
            WHERE m.url = :imageUrl
            """)
    void deleteByUrl(@Param("imageUrl") String imageUrl);
}
