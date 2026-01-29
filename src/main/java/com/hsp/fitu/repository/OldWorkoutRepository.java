package com.hsp.fitu.repository;

import com.hsp.fitu.entity.OldWorkoutEntity;
import com.hsp.fitu.entity.enums.Workout;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface OldWorkoutRepository extends JpaRepository<OldWorkoutEntity, Long> {
    OldWorkoutEntity findByName(Workout name);


}
