package com.hsp.fitu.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "post_comments")
public class PostCommentEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long postId;
    private Long writerId;
    private Long rootId;
    private String contents;

    @CreationTimestamp
    private LocalDateTime createdAt;

    public void update(String contents) {
        this.contents = contents;
    }
    public void setRootId(Long rootId) { this.rootId = rootId; }
}
