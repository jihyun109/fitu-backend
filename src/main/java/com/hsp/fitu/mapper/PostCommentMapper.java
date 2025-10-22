package com.hsp.fitu.mapper;

import com.hsp.fitu.dto.PostCommentResponseDTO;
import com.hsp.fitu.entity.PostCommentsEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PostCommentMapper {
    PostCommentResponseDTO commentToDTO(PostCommentsEntity postCommentsEntity);
}