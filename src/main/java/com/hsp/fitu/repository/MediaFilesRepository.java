package com.hsp.fitu.repository;

import com.hsp.fitu.entity.MediaFilesEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MediaFilesRepository extends JpaRepository<MediaFilesEntity, Long> {

}
