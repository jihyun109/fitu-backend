package com.hsp.fitu.repository;

import com.hsp.fitu.entity.UniversityEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface UniversityRepository extends JpaRepository<UniversityEntity, Long> {

    @Query("SELECT u.id FROM UniversityEntity u WHERE u.domainName = :domainName")
    Long findIdByDomainName(String domainName);
}
