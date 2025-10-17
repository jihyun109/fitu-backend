package com.hsp.fitu.controller;

import com.hsp.fitu.dto.BodyImageDeleteRequestDTO;
import com.hsp.fitu.dto.BodyImageMainResponseDTO;
import com.hsp.fitu.dto.ProfileImageUploadResponseDTO;
import com.hsp.fitu.entity.BodyImageEntity;
import com.hsp.fitu.jwt.CustomUserDetails;
import com.hsp.fitu.service.ProfileImageService;
import com.hsp.fitu.service.S3ImageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/v2/profile-image")
@RequiredArgsConstructor
@Slf4j
public class ProfileImageController {
    private final ProfileImageService profileImageService;
    private final S3ImageService s3ImageService;

    @GetMapping()
    public ResponseEntity<BodyImageMainResponseDTO> getMainProfileImage(@AuthenticationPrincipal CustomUserDetails userDetails) {
        Long userId = userDetails.getId();
        return ResponseEntity.ok(profileImageService.getMainProfileImage(userId));
    }

    @GetMapping("/history")
    public ResponseEntity<List<BodyImageEntity>> getBodyImages(@AuthenticationPrincipal CustomUserDetails userDetails) {
        Long userId = userDetails.getId();

        List<BodyImageEntity> bodyImageEntityList = profileImageService.getProfileImages(userId);
        return ResponseEntity.ok(bodyImageEntityList);
    }

    @PostMapping()
    public ResponseEntity<ProfileImageUploadResponseDTO> uploadBodyImage(@AuthenticationPrincipal CustomUserDetails userDetails, @RequestPart(value = "image", required = false) MultipartFile image) {
        Long userId = userDetails.getId();
        String url = s3ImageService.upload(image, userId);

        return ResponseEntity.ok(ProfileImageUploadResponseDTO.builder()
                .imageUrl(url).build());
    }

    @DeleteMapping()
    public ResponseEntity<?> s3delete(@RequestBody BodyImageDeleteRequestDTO dto) {
        s3ImageService.deleteImageFromS3(dto);
        return ResponseEntity.ok("Body image deleted successfully.");
    }
}