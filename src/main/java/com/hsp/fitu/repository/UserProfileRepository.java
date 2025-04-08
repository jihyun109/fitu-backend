package com.hsp.fitu.repository;

import com.hsp.fitu.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserProfileRepository extends JpaRepository<UserEntity, Long> {
}
