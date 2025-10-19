package com.hsp.fitu.dto;

import java.time.LocalDateTime;

public record PostCommentResponseDTO(
        Long id,
        String writerName,
        String writerProfileImgUrl,
        Long rootId,
        String contents,
        LocalDateTime createdAt,
        Boolean isMine,     //게시글 작성자가 작성한 댓글인지
        Boolean isSecret    //댓글이 비밀댓글인지
){}
