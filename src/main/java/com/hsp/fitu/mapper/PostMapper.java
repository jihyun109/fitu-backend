package com.hsp.fitu.mapper;

import com.hsp.fitu.dto.PostResponseDTO;
import com.hsp.fitu.entity.PostEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PostMapper {

    PostResponseDTO postToDTO(PostEntity postEntity);

}
