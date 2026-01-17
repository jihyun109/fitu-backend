package com.hsp.fitu.validator;

import com.hsp.fitu.entity.enums.MediaType;
import com.hsp.fitu.error.BusinessException;
import com.hsp.fitu.error.ErrorCode;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.List;

@Component
public class MediaValidator {
    private List<String> allowedImageExtentions = Arrays.asList("jpg", "jpeg", "png", "gif");
    private List<String> allowedVideoExtentions = Arrays.asList("mp4", "mov", "wmv", "avi");

    public void validateMedia(MultipartFile mediaFile, MediaType mediaType) {
        if (mediaFile == null || mediaFile.isEmpty()) {
            throw new BusinessException(ErrorCode.EMPTY_FILE);
        }

        String filename = mediaFile.getOriginalFilename();    // 파일 이름
        int lastDotIndex = filename != null ? filename.lastIndexOf(".") : -1;

        // 파일의 확장자가 없는 경우
        if (lastDotIndex == -1) {
            throw new BusinessException(ErrorCode.MISSING_FILE_EXTENSION);
        }

        // 확장자가 유효한지 확인
        switch (mediaType) {
            case IMAGE:
                if (!allowedImageExtentions.contains(filename.substring(lastDotIndex + 1).toLowerCase())) {
                    throw new BusinessException(ErrorCode.INVALID_FILE_EXTENSION);
                }
                break;
            case VIDEO:
                if (!allowedVideoExtentions.contains(filename.substring(lastDotIndex + 1).toLowerCase())) {
                    throw new BusinessException(ErrorCode.INVALID_FILE_EXTENSION);
                }
                break;
        }
    }
}
