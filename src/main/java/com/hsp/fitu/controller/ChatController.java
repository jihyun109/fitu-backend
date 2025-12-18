package com.hsp.fitu.controller;

import com.hsp.fitu.dto.ChatRoomCreateRequestDTO;
import com.hsp.fitu.dto.ChatRoomCreateResponseDTO;
import com.hsp.fitu.dto.ChatRoomListResponseDTO;
import com.hsp.fitu.dto.ChatRoomMessageResponseDTO;
import com.hsp.fitu.jwt.CustomUserDetails;
import com.hsp.fitu.service.ChatMessageService;
import com.hsp.fitu.service.ChatRoomService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v2/chat")
public class ChatController {

    private final ChatRoomService chatRoomService;
    private final ChatMessageService chatMessageService;

    @PostMapping("/room")
    @Operation(summary = "채팅방 생성 by 장지현")
    public ResponseEntity<ChatRoomCreateResponseDTO> createChatRoom(@AuthenticationPrincipal CustomUserDetails userDetails, @RequestBody ChatRoomCreateRequestDTO chatRoomCreateRequestDTO) {

        return ResponseEntity.ok(chatRoomService.createChatRoom(userDetails.getId(), chatRoomCreateRequestDTO));
    }

    @GetMapping("/room/list")
    @Operation(summary = "사용자의 채팅방 list 조회 by 장지현")
    public ResponseEntity<ChatRoomListResponseDTO> getChatRoomList(@AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(chatRoomService.getChatRoomList(userDetails.getId()));
    }

    @GetMapping("/message/{chatRoomId}")
    @Operation(summary = "채팅방 메시지 조회 by 장지현")
    public ResponseEntity<ChatRoomMessageResponseDTO> getChatMessage(@PathVariable Long chatRoomId) {

        return ResponseEntity.ok(chatMessageService.getChatRoomMessage(chatRoomId));
    }
}
