package com.hsp.fitu.controller;

import com.hsp.fitu.dto.BodyImageDeleteRequestDTO;
import com.hsp.fitu.dto.BodyImageMainResponseDTO;
import com.hsp.fitu.dto.BodyImageUploadResponseDTO;
import com.hsp.fitu.entity.BodyImageEntity;
import com.hsp.fitu.jwt.CustomUserDetails;
import com.hsp.fitu.service.BodyImageService;
import com.hsp.fitu.service.S3ImageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/body-image")
@RequiredArgsConstructor
@Slf4j
public class BodyImageController {
    private final BodyImageService bodyImageService;
    private final S3ImageService s3ImageService;

    @GetMapping()
    public ResponseEntity<BodyImageMainResponseDTO> getMainBodyPhoto(@AuthenticationPrincipal CustomUserDetails userDetails) {
        Long userId = userDetails.getId();
        return ResponseEntity.ok(bodyImageService.getMainBodyImage(userId));
    }

    @GetMapping("/history")
    public ResponseEntity<List<BodyImageEntity>> getBodyImages(@AuthenticationPrincipal CustomUserDetails userDetails) {
        Long userId = userDetails.getId();

        List<BodyImageEntity> bodyImageEntityList = bodyImageService.getBodyImages(userId);
        return ResponseEntity.ok(bodyImageEntityList);
    }

    @PostMapping()
    public ResponseEntity<BodyImageUploadResponseDTO> uploadBodyImage(@AuthenticationPrincipal CustomUserDetails userDetails, @RequestPart(value = "image", required = false) MultipartFile image) {
        Long userId = userDetails.getId();
        String url = s3ImageService.upload(image, userId);

        return ResponseEntity.ok(BodyImageUploadResponseDTO.builder()
                .imageUrl(url).build());
    }

    @DeleteMapping()
    public ResponseEntity<?> s3delete(@RequestBody BodyImageDeleteRequestDTO dto) {
        s3ImageService.deleteImageFromS3(dto);
        return ResponseEntity.ok("body image 삭제 완료");
    }
}