package com.hsp.fitu.mapper;

import com.hsp.fitu.dto.PostCommentResponseDTO;
import com.hsp.fitu.dto.PostResponseDTO;
import com.hsp.fitu.entity.PostEntity;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface PostMapper {

    PostResponseDTO postToDTO(PostEntity postEntity);

    PostResponseDTO postToDTO(PostEntity postEntity, List<PostCommentResponseDTO> commentDTOs);
}
