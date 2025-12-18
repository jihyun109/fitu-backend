package com.hsp.fitu.controller;

import com.hsp.fitu.dto.BodyImageDeleteRequestDTO;
import com.hsp.fitu.dto.ProfileImageResponseDTO;
import com.hsp.fitu.dto.ProfileImageUploadResponseDTO;
import com.hsp.fitu.dto.ProfileImagesResponseDTO;
import com.hsp.fitu.jwt.CustomUserDetails;
import com.hsp.fitu.service.ProfileImageService;
import com.hsp.fitu.service.S3Service;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v2/profile-image")
@RequiredArgsConstructor
public class ProfileImageController {
    private final ProfileImageService profileImageService;
    private final S3Service s3Service;

    @GetMapping()
    public ResponseEntity<ProfileImageResponseDTO> getMainProfileImage(@AuthenticationPrincipal CustomUserDetails userDetails) {
        Long userId = userDetails.getId();
        return ResponseEntity.ok(profileImageService.getMainProfileImage(userId));
    }

    @GetMapping("/history")
    public ResponseEntity<ProfileImagesResponseDTO> getProfileImages(@AuthenticationPrincipal CustomUserDetails userDetails) {
        Long userId = userDetails.getId();

        ProfileImagesResponseDTO profileImages = profileImageService.getProfileImages(userId);
        return ResponseEntity.ok(profileImages);
    }

    @PostMapping()
    @Operation(summary = "프로필 사진 업로드 by 장지현")
    public ResponseEntity<ProfileImageUploadResponseDTO> uploadProfileImage(@AuthenticationPrincipal CustomUserDetails userDetails,
                                                                            @RequestPart(value = "image", required = false) MultipartFile image) {
        Long userId = userDetails.getId();

        String url = profileImageService.uploadProfileImage(image, userId);

        return ResponseEntity.ok(ProfileImageUploadResponseDTO.builder()
                .imageUrl(url).build());
    }

    @DeleteMapping()
    public ResponseEntity<?> s3delete(@RequestBody BodyImageDeleteRequestDTO dto) {
        s3Service.deleteImageFromS3(dto);
        return ResponseEntity.ok("Body image deleted successfully.");
    }
}