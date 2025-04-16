package com.hsp.fitu.controller;

import com.hsp.fitu.dto.BodyImageMainResponseDTO;
import com.hsp.fitu.repository.BodyImageRepository;
import com.hsp.fitu.service.BodyImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/body-image")
@RequiredArgsConstructor
public class BodyImageController {
    private final BodyImageService bodyImageService;
    @RequestMapping("/{userId}")
    public ResponseEntity<BodyImageMainResponseDTO> getMainBodyPhoto(@PathVariable long userId) {
        return ResponseEntity.ok(bodyImageService.getMainBodyImage(userId));
    }
}
