package com.hsp.fitu.controller;

import com.hsp.fitu.dto.BodyImageMainResponseDTO;
import com.hsp.fitu.dto.BodyImageUploadResponseDTO;
import com.hsp.fitu.entity.BodyImageEntity;
import com.hsp.fitu.service.BodyImageService;
import com.hsp.fitu.service.S3ImageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Controller
@RequestMapping("/body-image")
@RequiredArgsConstructor
@Slf4j
public class BodyImageController {
    private final BodyImageService bodyImageService;
    private final S3ImageService s3ImageService;

    @GetMapping("/{userId}")
    public ResponseEntity<BodyImageMainResponseDTO> getMainBodyPhoto(@PathVariable long userId) {
        return ResponseEntity.ok(bodyImageService.getMainBodyImage(userId));
    }

    @GetMapping("/{userId}/history")
    public ResponseEntity<List<BodyImageEntity>> getBodyImages(@PathVariable long userId) {
        log.info("userID:" + userId);
        List<BodyImageEntity> bodyImageEntityList = bodyImageService.getBodyImages(userId);
        return ResponseEntity.ok(bodyImageEntityList);
    }

    @PostMapping("/{userId}/body-image")
    public ResponseEntity<BodyImageUploadResponseDTO> uploadBodyImage(@PathVariable long userId, @RequestPart(value = "image", required = false) MultipartFile image) {
        String url = s3ImageService.upload(image, userId);

        return ResponseEntity.ok(BodyImageUploadResponseDTO.builder()
                .imageUrl(url).build());
    }

    @DeleteMapping("{userId}/body-image")
    public ResponseEntity<?> s3delete(@RequestParam String addr) {
        s3ImageService.deleteImageFromS3(addr);
        return ResponseEntity.ok("body image 삭제 완료");
    }
}