package com.hsp.fitu.controller;

import com.hsp.fitu.dto.AdminPostManagementRequestDTO;
import com.hsp.fitu.dto.AdminPostManagementResponseDTO;
import com.hsp.fitu.dto.PostSliceResponseDTO;
import com.hsp.fitu.service.AdminPostManagementService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v2/admin")
@RequiredArgsConstructor
public class AdminPostManagementController {
    private final AdminPostManagementService adminPostManagementService;

    @Operation(summary = "게시물 관리 by 조민기")
    @PostMapping("/by-university")
    public ResponseEntity<PostSliceResponseDTO<AdminPostManagementResponseDTO>> getPostsByUniversity(
            @RequestBody AdminPostManagementRequestDTO requestDTO,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size
    ) {
        PostSliceResponseDTO<AdminPostManagementResponseDTO> posts =
                adminPostManagementService.getPostsByUniversity(requestDTO, page, size);
        return ResponseEntity.ok(posts);
    }

}
