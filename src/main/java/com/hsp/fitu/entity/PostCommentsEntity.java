package com.hsp.fitu.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Getter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "post_comments")
public class PostCommentsEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "post_id")
    private long postId;

    @Column(name = "writer_id")
    private long writerId;

    @Column(name = "root_id")
    private long rootId;

    @Column(name = "contents")
    private String contents;

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "is_secret")
    private Boolean isSecret;

    public void update(String contents) {
        this.contents = contents;
    }

    public void setRootId(Long rootId) {
        this.rootId = rootId;
    }
}
