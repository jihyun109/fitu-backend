package com.hsp.fitu.entity;

import com.hsp.fitu.entity.enums.PostCategory;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "posts")
public class PostEntity {
    @Id
    private Long id;

    @Enumerated(EnumType.STRING)
    private PostCategory category;

    private Long universityId;
    private Long writerId;
    private String title;
    private String content;
    private LocalDateTime createdAt;

    @Builder
    public PostEntity(PostCategory category, Long universityId, Long writerId, String title, String content) {

    }

    // 게시글 업데이트 로직을 엔티티 자체에 캡슐화
    public void update(PostCategory category, Long universityId, String title, String content) {
        this.category = category;
        this.universityId = universityId;
        this.title = title;
        this.content = content;
    }
}
