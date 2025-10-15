package com.hsp.fitu.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
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

    public void update(String contents) {
        this.contents = contents;
    }

    public void setRootId(Long rootId) {
        this.rootId = rootId;
    }
}
