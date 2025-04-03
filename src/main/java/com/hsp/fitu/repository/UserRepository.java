package com.hsp.fitu.repository;

import com.hsp.fitu.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, Long> {
    Optional<UserEntity> findByKakaoEmail(String kakaoEmail);
}
