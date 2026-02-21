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

/**
 * 채팅 REST API 컨트롤러.
 * 채팅방 생성/조회, 메시지 이력 조회를 담당한다.
 * 실시간 메시지 송수신은 WebSocket(STOMP)을 사용하므로 여기에 없다.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v2/chat")
public class ChatController {

    private final ChatRoomService chatRoomService;
    private final ChatMessageService chatMessageService;

    /**
     * 채팅방 생성.
     * 요청자는 자동으로 멤버에 포함되며, memberIds[0]의 프로필 이미지가 채팅방 썸네일로 설정된다.
     */
    @PostMapping("/room")
    @Operation(summary = "채팅방 생성 by 장지현")
    public ResponseEntity<ChatRoomCreateResponseDTO> createChatRoom(@AuthenticationPrincipal CustomUserDetails userDetails, @RequestBody ChatRoomCreateRequestDTO chatRoomCreateRequestDTO) {

        return ResponseEntity.ok(chatRoomService.createChatRoom(userDetails.getId(), chatRoomCreateRequestDTO));
    }

    /**
     * 내가 속한 채팅방 목록 조회.
     * 각 채팅방의 썸네일 이미지 URL도 함께 반환한다.
     */
    @GetMapping("/room/list")
    @Operation(summary = "사용자의 채팅방 list 조회 by 장지현")
    public ResponseEntity<ChatRoomListResponseDTO> getChatRoomList(@AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(chatRoomService.getChatRoomList(userDetails.getId()));
    }

    /**
     * 특정 채팅방의 메시지 이력 조회.
     * 채팅방 입장 시 과거 메시지를 불러올 때 사용한다.
     */
    @GetMapping("/message/{chatRoomId}")
    @Operation(summary = "채팅방 메시지 조회 by 장지현")
    public ResponseEntity<ChatRoomMessageResponseDTO> getChatMessage(@PathVariable Long chatRoomId) {

        return ResponseEntity.ok(chatMessageService.getChatRoomMessage(chatRoomId));
    }
}
