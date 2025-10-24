package com.hsp.fitu.dto;

import java.util.List;

public record PostDetailResponseDTO(
        PostResponseDTO post,
        List<PostCommentResponseDTO> comments
) {
}
