package com.hsp.fitu.service;

import com.hsp.fitu.dto.AdminSuspendRequestDTO;
import com.hsp.fitu.dto.AdminUserResponseDTO;
import com.hsp.fitu.entity.UserEntity;
import com.hsp.fitu.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminUserServiceImpl implements AdminUserService{
    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public List<AdminUserResponseDTO> searchUsersByName(String name) {
        return userRepository.findByNameContaining(name);
    }

    @Override
    @Transactional
    public void suspendUser(Long userId, AdminSuspendRequestDTO dto) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));

        LocalDateTime suspendEndAt = LocalDateTime.now().plusDays(dto.suspendDays());

        user.suspend(suspendEndAt);
        userRepository.save(user);
    }

    @Override
    @Transactional
    public void unsuspendUser(Long userId) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));
        user.unsuspend();
        userRepository.save(user);
    }
}
