package com.hsp.fitu.controller;

import com.hsp.fitu.dto.ApiResponseDTO;
import com.hsp.fitu.dto.FriendAddRequestDTO;
import com.hsp.fitu.jwt.CustomUserDetails;
import com.hsp.fitu.service.FriendService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/friend")
public class FriendController {
    private final FriendService friendService;

    @PostMapping()
    @Operation(summary = "친구 추가 by 장지현",
            description = "친구 코드를 사용하여 새로운 친구를 추가합니다")
    public ResponseEntity<ApiResponseDTO> addFriend(@RequestBody FriendAddRequestDTO requestDTO, @AuthenticationPrincipal CustomUserDetails userDetails) {
        Long userId = userDetails.getId();

        friendService.addFriend(requestDTO, userId);

        return ResponseEntity.ok(new ApiResponseDTO("친구 추가가 완료되었습니다"));
    }
}