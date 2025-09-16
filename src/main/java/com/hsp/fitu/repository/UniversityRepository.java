package com.hsp.fitu.repository;

import com.hsp.fitu.entity.PhysicalInfoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UniversityRepository extends JpaRepository<PhysicalInfoEntity, Long> {

}
