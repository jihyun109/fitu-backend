package com.hsp.fitu.mapper;

import com.hsp.fitu.dto.PostResponseDTO;
import com.hsp.fitu.entity.PostEntity;

public class PostMapper {
    public static PostResponseDTO postToDTO(PostEntity post) {
        return PostResponseDTO.builder()
                .id(post.getId())
                .category(post.getCategory())
                .title(post.getTitle())
                .contents(post.getContents())
                .build();
    }
}
